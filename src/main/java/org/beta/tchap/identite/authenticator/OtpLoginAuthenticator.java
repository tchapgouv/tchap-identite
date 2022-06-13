package org.beta.tchap.identite.authenticator;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.beta.tchap.identite.email.EmailSender;
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
import org.keycloak.sessions.AuthenticationSessionModel;

/** Send a OTP to the user in session authenticate() and wait for it in action() */
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

    public OtpLoginAuthenticator(
            SecureCode secureCode, EmailSender emailSender, int codeTimeout, int mailDelay) {
        this.secureCode = secureCode;
        this.emailSender = emailSender;
        this.codeTimeout = codeTimeout;
        this.mailDelay = mailDelay;
    }

    /** Send a otp to the user in session and present a form */
    @Override
    public void authenticate(AuthenticationFlowContext context) {
        String loginHint = context.getAuthenticationSession().getAuthNote(AUTH_NOTE_USER_EMAIL);

        if (LOG.isDebugEnabled()) {
            LOG.debugf("Authenticate OtpLoginAuthenticator");
        }

        if (!canSendNewCode(context)) {
            if (LOG.isDebugEnabled()) {
                LOG.debugf(
                        "Authenticate login : %s, a previous code has been sent. Should wait for"
                                + " cool down delay before sending a new one",
                        LoggingUtilsFactory.getInstance().logOrHash(loginHint));
            }

            context.challenge(
                    context.form()
                            .setAttribute(FORM_ATTRIBUTE_USER_EMAIL, loginHint)
                            .setInfo("info.code.already.sent.wait", mailDelay)
                            .createForm(FTL_ENTER_CODE));
            return;
        }

        if (generateAndSendCode(context)) {
            // code has been sent
            context.success();
        }

        context.challenge(otpForm(context, null));
    }

    /** Wait for the otp in the form input */
    @Override
    public void action(AuthenticationFlowContext context) {
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Authenticate action OtpLoginAuthenticator %s", context);
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

        // authenticator is successful
        context.setUser(getUser(context));
        context.success();
    }

    private UserModel getUser(AuthenticationFlowContext context) {
        return context.getSession()
                .users()
                .getUserByEmail(
                        context.getRealm(),
                        context.getAuthenticationSession().getAuthNote(AUTH_NOTE_USER_EMAIL));
    }

    /**
     * Prepare the view of the otp form
     *
     * @param context
     * @param info optional info message
     * @return ready-to-send Response
     */
    private Response otpForm(AuthenticationFlowContext context, String info) {
        String userEmail = context.getAuthenticationSession().getAuthNote(AUTH_NOTE_USER_EMAIL);

        /* if userEmail is not set in the authentication session, fails */
        if (userEmail == null || userEmail.isEmpty()) {
            // TODO
            userEmail = "";
        }

        /* display otp form*/
        LoginFormsProvider form = context.form().setAttribute(FORM_ATTRIBUTE_USER_EMAIL, userEmail);

        if (info != null && !info.isEmpty()) {
            form.setInfo(info);
        }
        return form.createForm(FTL_ENTER_CODE);
    }

    /**
     * Prepare an error view
     *
     * @param context
     * @param error required error message
     * @return ready-to-send Response
     */
    private Response otpFormError(AuthenticationFlowContext context, String error) {
        String userEmail = context.getAuthenticationSession().getAuthNote(AUTH_NOTE_USER_EMAIL);

        /* if userEmail is not set in the authentication session, fails */
        if (userEmail == null || userEmail.isEmpty()) {
            // TODO
            userEmail = "";
        }

        /* display otp form*/
        return context.form()
                .setAttribute(FORM_ATTRIBUTE_USER_EMAIL, userEmail)
                .setAttribute(FORM_ATTRIBUTE_ERROR_TYPE, error)
                .setError(error)
                .createForm(FTL_ENTER_CODE);
    }

    /**
     * Send a OTP to the user by email
     *
     * @param context keycloak auth context
     * @return true is email has been sent
     */
    private boolean generateAndSendCode(AuthenticationFlowContext context) {
        String code = secureCode.generateCode(6);
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
                getUser(context),
                friendlyCode,
                String.valueOf(codeTimeout))) {
            // error while sending email
            otpFormError(context, "error.email.not.sent");
            return false;
        }

        setCodeTimestamp(context);
        return true;
        /*
         * TODO: SEND CODE TO TCHAP ALSO
         */
    }

    /**
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
