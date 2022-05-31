package org.beta.tchap.identite.authenticator;

import org.beta.tchap.identite.email.EmailSender;
import org.beta.tchap.identite.utils.LoggingUtilsFactory;
import org.beta.tchap.identite.utils.SecureCode;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.ws.rs.core.Response;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.beta.tchap.identite.authenticator.OtpLoginAuthenticator.*;

public class TchapAuthenticator implements Authenticator {

    private final SecureCode secureCode;
    private final EmailSender emailSender;
    private static final String FTL_UNAUTHORIZED_USER       = "unauthorized-user.ftl";
    private static final Integer MAX_LOGIN_HINTS_OCCURENCE_FOR_ONE_BROWSER = 10;
    private static final Integer SEND_CODE_COOLDOWN_IN_MINUTES = 1;
    private static final String SEND_CODE_TIMESTAMP = "send-code-timestamp";


    private static final Logger LOG = Logger.getLogger(TchapAuthenticator.class);

    TchapAuthenticator(EmailSender emailSender, SecureCode secureCode){
        this.secureCode = secureCode;
        this.emailSender = emailSender;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticationSessionModel session  = context.getAuthenticationSession();
        String loginHint = session.getClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM);

        if(loginHint == null){
            context.failure(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR,
                    Response.status(400).build(), "request is malformed", "request is malformed" );
            return;
        }

        if(tooManyLoginHints(context)){
            LOG.warnf("Authenticate login : %s, parent session has used too many different loginHints",
                    LoggingUtilsFactory.getInstance().logOrHash(loginHint));
            context.challenge(otpForm(context,"Nous avons détecté de multiples tentatives de login différentes depuis ce poste, veuillez contacter un administrateur audioConf pour continuer"));

            return;
        }

        if(!canSendNewCode(context)){
            if(LOG.isDebugEnabled()){LOG.debugf("Authenticate login : %s, a previous code has been sent. Should wait for cool down delay before sending a new one",
                    LoggingUtilsFactory.getInstance().logOrHash(loginHint));}

            context.challenge(otpForm(context,
                    String.format("Un code vous a déjà été envoyé, veuillez attendre %s minute avant de demander un nouveau code", SEND_CODE_COOLDOWN_IN_MINUTES)));
            return;
        }

        if(LOG.isDebugEnabled()){LOG.debugf("Authenticate login : %s, AuthenticationSession.TabId : %s, ParentSession.Id %s",
                    LoggingUtilsFactory.getInstance().logOrHash(loginHint),
                    context.getAuthenticationSession().getTabId(),
                    context.getAuthenticationSession().getParentSession().getId());
        }

        //set loginHint in keycloak authentication session (attached to browser>tab via cookie)
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE_USER_EMAIL, loginHint);
        UserModel user = getUser(context);

        if (user == null) {
            showUnauthorizedUser(context);
            return;
        }

        //user has been found
        if(generateAndSendCode(context)){
            //code has been sent
            context.success();
        }
    }

    /**
     * Check if a new code can be sent. A cool down delay must be respected.
     * @param context keycloak auth context
     * @return true/false
     */
    boolean canSendNewCode(AuthenticationFlowContext context) {
        long timestamp = getLastCodeTimestamp(context);
        if(LOG.isDebugEnabled()){ LOG.debugf("Last timestamp found in authentication sessions note %s", timestamp);}

        return timestamp == 0 || (Instant.now().toEpochMilli() - timestamp) > SEND_CODE_COOLDOWN_IN_MINUTES * 60 * 1000;
    }

    /* set timestamp in auth session */
    private void setCodeTimestamp(AuthenticationFlowContext context){
        context.getAuthenticationSession().setAuthNote(SEND_CODE_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
    }

    /**
     * check that occurences login hints have been used from authentication sessions from this
     * browser is not superior than MAX_LOGIN_HINTS_OCCURENCE_FOR_ONE_BROWSER
     * @param context keycloak auth context
     * @return number of different login hints
     */
    private boolean tooManyLoginHints(AuthenticationFlowContext context){
        Set<String> loginHints = new HashSet<>();
        for(AuthenticationSessionModel session : context.getAuthenticationSession().getParentSession().getAuthenticationSessions().values()){
            String loginHint = session.getAuthNote(AUTH_NOTE_USER_EMAIL);
            if(loginHint!=null){
                loginHints.add(loginHint);
            }
        }
        return loginHints.size() > MAX_LOGIN_HINTS_OCCURENCE_FOR_ONE_BROWSER;
    }

    /**
     * Return last code timestamp from authentication sessions from this browser
     * @param context keycloak auth context
     * @return timestamp or 0 if none
     */
    private long getLastCodeTimestamp(AuthenticationFlowContext context){
        Set<Long> timestamps = new HashSet<>();
        for(AuthenticationSessionModel session :
                context.getAuthenticationSession().getParentSession().getAuthenticationSessions().values()){
            String timestampString = session.getAuthNote(SEND_CODE_TIMESTAMP);
            if(timestampString!=null){
                timestamps.add(Long.parseLong(timestampString));
            }
        }
        if(timestamps.isEmpty()){
            return 0;
        }
        return Collections.max(timestamps);
    }


    private void showUnauthorizedUser(AuthenticationFlowContext context){
        context.failure(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR,  context.form()
                .createForm(FTL_UNAUTHORIZED_USER)
                , "unknown user", "unknow user");
    }

    /**
     * Send a OTP to the user by email
     * @param context keycloak auth context
     * @return true is email has been sent
     */
    private boolean generateAndSendCode(AuthenticationFlowContext context){
        String code = secureCode.generateCode(6);
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE_OTP, code);
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE_TIMESTAMP,
                Long.toString(System.currentTimeMillis()));

        String friendlyCode = secureCode.makeCodeUserFriendly(code);
        if(LOG.isDebugEnabled()){
            LOG.debugf("Sending OTP : %s", LoggingUtilsFactory.getInstance().logOrHide(friendlyCode));
        }
        if(!emailSender.sendEmail(context.getSession(), context.getRealm(),
                              getUser(context), friendlyCode)){
            //error while sending email
            otpFormError(context, "Impossible de vous envoyer l'email avec le code de connection, veuillez réessayer");
            return false;
        }

        setCodeTimestamp(context);
        return true;
        /*
         * TODO: SEND CODE TO TCHAP ALSO
         */
    }

    private UserModel getUser(AuthenticationFlowContext context){
        return context.getSession().users().getUserByEmail(context.getRealm(),
                context.getAuthenticationSession().getAuthNote(AUTH_NOTE_USER_EMAIL));
    }


    @Override
    public void action(AuthenticationFlowContext context) {

    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

    }

    @Override
    public void close() {

    }
}
