package org.beta.tchap.identite.authenticator;

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

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * If all conditions for the code validation are met he gets logged in
 * else he has to restart the process.
 */

public class OtpLoginAuthenticator
    implements Authenticator
{
    private static final Logger LOG = Logger.getLogger(OtpLoginAuthenticator.class);

    private static final String FTL_ENTER_CODE       = "enter-code.ftl";
    public static final String AUTH_NOTE_USER_EMAIL = "user-email";
    public static final String AUTH_NOTE_OTP = "email-code";
    public static final String AUTH_NOTE_TIMESTAMP  = "timestamp";
    public static final String FORM_ATTRIBUTE_USER_EMAIL  = "userEmail";

    private static final Integer CODE_ACTIVATION_DELAY_IN_SECONDS = 2;

    private static final String SEND_CODE_TIMESTAMP = "send-code-timestamp";
    
    
    private final SecureCode secureCode;
    private final EmailSender emailSender;
    private final int codeTimeout;
    private final int mailDelay;

    public OtpLoginAuthenticator(SecureCode secureCode, EmailSender emailSender, int codeTimeout, int mailDelay)
    {
        this.secureCode = secureCode;
        this.emailSender = emailSender;
        this.codeTimeout = codeTimeout;
        this.mailDelay = mailDelay;
    }

    @Override
    public void action(AuthenticationFlowContext context)
    {
        if(LOG.isDebugEnabled()){
            LOG.debugf("Authenticate action OtpLoginAuthenticator %s", context);
        }

        /* retrieve formData*/
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String codeInput = formData.getFirst("codeInput");

        if (codeInput == null || codeInput.isEmpty()) {
            context.challenge(otpForm(context,"Veuillez renseignez un code"));
            return;
        }

        if (context.getAuthenticationSession().getAuthNote(AUTH_NOTE_OTP) == null) {
            context.challenge(otpFormError(context,"Le code n'est pas valide. Vérifiez votre saisie ou demandez un nouveau code."));
            return;
        }

        //trim code
        codeInput = codeInput.trim();

        if (!secureCode.isValid(codeInput, context.getAuthenticationSession().getAuthNote(AUTH_NOTE_OTP),
                               context.getAuthenticationSession().getAuthNote(AUTH_NOTE_TIMESTAMP),
                               codeTimeout,
                CODE_ACTIVATION_DELAY_IN_SECONDS)) {
            //code validation has failed
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS,otpFormError(context,"Le code n'est pas valide. Vérifiez votre saisie ou demandez un nouveau code."));
            return;
        }

        context.setUser(getUser(context));
        context.success();
    }

    private UserModel getUser(AuthenticationFlowContext context)
    {
        return context.getSession().users().getUserByEmail(context.getAuthenticationSession().getAuthNote(AUTH_NOTE_USER_EMAIL), context.getRealm());
    }

    @Override
    public void authenticate(AuthenticationFlowContext context)
    {
        String loginHint = context.getAuthenticationSession().getAuthNote(AUTH_NOTE_USER_EMAIL);
        String infoMessage = "";

        if(LOG.isDebugEnabled()){
            LOG.debugf("Authenticate OtpLoginAuthenticator");
        }
        
        if(!canSendNewCode(context)){
            if(LOG.isDebugEnabled()){LOG.debugf("Authenticate login : %s, a previous code has been sent. Should wait for cool down delay before sending a new one",
                    LoggingUtilsFactory.getInstance().logOrHash(loginHint));}

            infoMessage = 
                    String.format("Un code vous a déjà été envoyé, veuillez attendre %s minute avant de demander un nouveau code.", mailDelay);
            ;
        }else{

            if(generateAndSendCode(context)){
                //code has been sent
                context.success();
            }
        }

        context.challenge(otpForm(context,infoMessage));
    }

    static Response otpForm(AuthenticationFlowContext context, String info){
        String userEmail = context.getAuthenticationSession().getAuthNote(AUTH_NOTE_USER_EMAIL);

        /* if userEmail is not set in the authentication session, fails */
        if(userEmail==null || userEmail.isEmpty()){
            //TODO
        }

        /* display otp form*/
        LoginFormsProvider form =  context.form()
                .setAttribute(FORM_ATTRIBUTE_USER_EMAIL, userEmail);

        if(info !=null && !info.isEmpty()){
            form.setInfo(info);
        }
        return form.createForm(FTL_ENTER_CODE);
    }

    static Response otpFormError(AuthenticationFlowContext context, String error){
        String userEmail = context.getAuthenticationSession().getAuthNote(AUTH_NOTE_USER_EMAIL);

        /* if userEmail is not set in the authentication session, fails */
        if(userEmail==null || userEmail.isEmpty()){
            //TODO
        }

        /* display otp form*/
        return context.form()
                .setAttribute(FORM_ATTRIBUTE_USER_EMAIL, userEmail)
                .setError(error)
                .createForm(FTL_ENTER_CODE);
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
                              getUser(context), friendlyCode, String.valueOf(codeTimeout))){
            //error while sending email
            otpFormError(context, "Impossible de vous envoyer le mail avec le code de connection, veuillez réessayer.");
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
     * @param context keycloak auth context
     * @return true/false
     */
    boolean canSendNewCode(AuthenticationFlowContext context) {
        long timestamp = getLastCodeTimestamp(context);
        if(LOG.isDebugEnabled()){ LOG.debugf("Last timestamp found in authentication sessions note %s", timestamp);}

        return timestamp == 0 || (Instant.now().toEpochMilli() - timestamp) > mailDelay * 60 * 1000;
    }


    /* set timestamp in auth session */
    private void setCodeTimestamp(AuthenticationFlowContext context){
        context.getAuthenticationSession().setAuthNote(SEND_CODE_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
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

    @Override
    public boolean requiresUser()
    {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user)
    {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user)
    {
        // not needed for current version
    }

    @Override
    public void close()
    {
        // not used for current version
    }

}
