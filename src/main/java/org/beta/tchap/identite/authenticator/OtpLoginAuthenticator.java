package org.beta.tchap.identite.authenticator;

import org.beta.tchap.identite.bot.BotSender;
import org.beta.tchap.identite.email.EmailSender;
import org.beta.tchap.identite.user.TchapUserStorage;
import org.beta.tchap.identite.utils.Features;
import org.beta.tchap.identite.utils.LoggingUtilsFactory;
import org.beta.tchap.identite.utils.SecureCode;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.BruteForceProtector;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Send a OTP to the user in session authenticate() and wait for it in action()
 */
public class OtpLoginAuthenticator implements Authenticator {
    private static final Logger LOG = Logger.getLogger(OtpLoginAuthenticator.class);

    private static final String FTL_ENTER_CODE = "enter-code.ftl";
    public static final String AUTH_NOTE_USER_EMAIL = "user-email";
    public static final String AUTH_NOTE_OTP = "email-code";
    public static final String AUTH_NOTE_TIMESTAMP = "timestamp";
    public static final String FORM_ATTRIBUTE_USER_EMAIL = "userEmail";
    public static final String FORM_ATTRIBUTE_ERROR_TYPE = "errorType";

    private static final Integer CODE_ACTIVATION_DELAY_IN_SECONDS = 2;

    private static final String SEND_CODE_TIMESTAMP = "send-code-timestamp";

    private final SecureCode secureCode;
    private final EmailSender emailSender;
    private final int codeTimeout;
    private final int mailDelay;
    private final BotSender botSender;

    public OtpLoginAuthenticator(
            SecureCode secureCode, EmailSender emailSender, int codeTimeout, int mailDelay, BotSender botSender) {
        this.secureCode = secureCode;
        this.emailSender = emailSender;
        this.codeTimeout = codeTimeout;
        this.mailDelay = mailDelay;
        this.botSender = botSender;
    }

    /**
     * Send a otp to the user in session and present a form
     */
    @Override
    public void authenticate(AuthenticationFlowContext context) {
        //user should have been set in the context before
        UserModel user = context.getUser();

        if (user == null) {
            context.failure(AuthenticationFlowError.UNKNOWN_USER);
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debugf("Authenticate OtpLoginAuthenticator with user %s %s", LoggingUtilsFactory.getInstance().logOrHash(user.getEmail()),
                    user.getFirstAttribute(TchapUserStorage.ATTRIBUTE_HOMESERVER));
        }

        if (isTemporarilyDisabled(context)) {
            LOG.warnf("User is temporarily disabled  %s", user.getId());
            // in case of spamming, no code will be sent and the user will be ignored silently
            // we still treat this scenario as a success to do disturb the flow for the clients
            context.challenge(otpForm(context, null));
            return;
        }

        if (!canSendNewCode(context)) {
            if (LOG.isDebugEnabled()) {
                LOG.debugf(
                        "Authenticate login : %s, a previous code has been sent. Should wait for"
                                + " cool down delay before sending a new one",
                        LoggingUtilsFactory.getInstance().logOrHash(user.getUsername()));
            }

            context.challenge(
                    context.form()
                            .setAttribute(FORM_ATTRIBUTE_USER_EMAIL, user.getUsername())
                            .setAttribute("feature_tchap_bot", Features.isTchapBotEnabled())
                            .setInfo("info.code.already.sent.wait", mailDelay)
                            .createForm(FTL_ENTER_CODE));
            return;
        }


        if (generateAndSendCode(context)) {
            //add a message if a code has already been sent
            String info = hasSentCode(context) ? "info.new.code.sent" : null;
            // code has been sent, succes, add a timestamp in session
            setCodeTimestamp(context);
            context.challenge(otpForm(context, info));
        } else {
            // error while sending email
            context.challenge(otpFormError(context, "error.email.not.sent"));
        }
    }

    /**
     * Wait for the otp in the form input
     */
    @Override
    public void action(AuthenticationFlowContext context) {
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Authenticate action OtpLoginAuthenticator %s", context);
        }

        if (isTemporarilyDisabled(context)) {
            LOG.warnf("User is temporarily disabled  %s", context.getUser().getId());
            // in case of spamming, the user will be ignored silently
            // we still treat this scenario as an invalid code scenario to do disturb the flow for the clients
            context.failureChallenge(
                    AuthenticationFlowError.INVALID_CREDENTIALS,
                    otpFormError(context, "error.invalid.code"));
            return;
        }

        /* retrieve formData*/
        MultivaluedMap<String, String> formData =
                context.getHttpRequest().getDecodedFormParameters();
        String codeInput = formData.getFirst("codeInput");

        if (codeInput == null || codeInput.isEmpty()) {
            context.challenge(otpForm(context, "info.input.code"));
            return;
        }

        if (context.getAuthenticationSession().getAuthNote(AUTH_NOTE_OTP) == null) {
            context.challenge(otpFormError(context, "error.invalid.code.in.session"));
            return;
        }

        // trim code
        codeInput = codeInput.trim();

        if (!secureCode.isValid(
                codeInput,
                context.getAuthenticationSession().getAuthNote(AUTH_NOTE_OTP),
                context.getAuthenticationSession().getAuthNote(AUTH_NOTE_TIMESTAMP),
                codeTimeout,
                CODE_ACTIVATION_DELAY_IN_SECONDS)) {
            // code validation has failed
            context.failureChallenge(
                    AuthenticationFlowError.INVALID_CREDENTIALS,
                    otpFormError(context, "error.invalid.code"));
            return;
        }

        context.success();
    }
/*
    private UserModel getUser(AuthenticationFlowContext context) {
        return context.getSession()
                .users()
                .getUserByEmail(
                        context.getRealm(),
                        context.getAuthenticationSession().getAuthNote(AUTH_NOTE_USER_EMAIL));
    }
 */

    /**
     * Prepare the view of the otp form
     *
     * @param context
     * @param info    optional info message
     * @return ready-to-send Response
     */
    private Response otpForm(AuthenticationFlowContext context, String info) {
        String userEmail = context.getUser().getUsername();

        /* display otp form*/
        LoginFormsProvider form = context.form()
                .setAttribute(FORM_ATTRIBUTE_USER_EMAIL, userEmail)
                .setAttribute("feature_tchap_bot", Features.isTchapBotEnabled());

        if (info != null && !info.isEmpty()) {
            form.setInfo(info);
        }
        return form.createForm(FTL_ENTER_CODE);
    }

    /**
     * Prepare an error view
     *
     * @param context
     * @param error   required error message
     * @return ready-to-send Response
     */
    private Response otpFormError(AuthenticationFlowContext context, String error) {
        String userEmail = context.getUser().getUsername();

        /* display otp form*/
        return context.form()
                .setAttribute(FORM_ATTRIBUTE_USER_EMAIL, userEmail)
                .setAttribute(FORM_ATTRIBUTE_ERROR_TYPE, error)
                .setAttribute("feature_tchap_bot", Features.isTchapBotEnabled())
                .setError(error)
                .createForm(FTL_ENTER_CODE);
    }

    /**
     * Send a OTP to the user by email
     *
     * @param context keycloak auth context
     * @return true if both email and tchap message have been sent
     */
    private boolean generateAndSendCode(AuthenticationFlowContext context) {
        String code = secureCode.generateCode(6);
        UserModel user = context.getUser();
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE_OTP, code);
        context.getAuthenticationSession()
                .setAuthNote(AUTH_NOTE_TIMESTAMP, Long.toString(System.currentTimeMillis()));

        String friendlyCode = secureCode.makeCodeUserFriendly(code);
        if (LOG.isDebugEnabled()) {
            LOG.debugf(
                    "Sending OTP : %s", LoggingUtilsFactory.getInstance().logOrHide(friendlyCode));
        }
        if (!emailSender.sendEmail(
                context.getSession(),
                context.getRealm(),
                context.getUser(),
                friendlyCode,
                String.valueOf(codeTimeout))) {
            return false;
        }

        if (Features.isTchapBotEnabled()) {
            // whatever is happening on the bot side, we do not fail the whole process as long the email has been sent
            botSender.sendMessage(
                    context.getAuthenticationSession().getClient().getName(),
                    user.getUsername(),
                    friendlyCode
            );
        }

        return true;
    }

    /**
     * Check if a code has already been sent
     *
     * @param context keycloak auth context
     * @return true/false
     */
    private boolean hasSentCode(AuthenticationFlowContext context) {
        return getLastCodeTimestamp(context) != 0;
    }

    /**
     * IMPORTANT : This feature is not stable, do not activate
     * <p>
     * Check if a new code can be sent. A cool down delay must be respected.
     *
     * @param context keycloak auth context
     * @return true/false
     */
    private boolean canSendNewCode(AuthenticationFlowContext context) {
        long timestamp = getLastCodeTimestamp(context);
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Last timestamp found in authentication sessions note %s", timestamp);
        }

        return timestamp == 0
                || (Instant.now().toEpochMilli() - timestamp) > (long) mailDelay * 60 * 1000;
    }

    /* set timestamp in auth session */
    private void setCodeTimestamp(AuthenticationFlowContext context) {
        context.getAuthenticationSession()
                .setAuthNote(SEND_CODE_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
    }

    /**
     * Return last code timestamp from authentication sessions from this browser
     *
     * @param context keycloak auth context
     * @return timestamp or 0 if none
     */
    private long getLastCodeTimestamp(AuthenticationFlowContext context) {
        Set<Long> timestamps = new HashSet<>();
        for (AuthenticationSessionModel session :
                context.getAuthenticationSession()
                        .getParentSession()
                        .getAuthenticationSessions()
                        .values()) {
            String timestampString = session.getAuthNote(SEND_CODE_TIMESTAMP);
            if (timestampString != null) {
                timestamps.add(Long.parseLong(timestampString));
            }
        }
        if (timestamps.isEmpty()) {
            return 0;
        }
        return Collections.max(timestamps);
    }

    private boolean isTemporarilyDisabled(AuthenticationFlowContext context) {
        BruteForceProtector bruteForceProtector = context.getSession().getProvider(BruteForceProtector.class);
        UserModel user = context.getUser();
        return bruteForceProtector.isTemporarilyDisabled(context.getSession(), context.getRealm(), user);
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // not needed for current version
    }

    @Override
    public void close() {
        // not used for current version
    }
}
