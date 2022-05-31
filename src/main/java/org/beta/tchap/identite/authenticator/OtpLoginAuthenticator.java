package org.beta.tchap.identite.authenticator;

import org.beta.tchap.identite.utils.SecureCode;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.forms.login.LoginFormsPages;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * If all conditions for the code validation are met he gets logged in
 * else he has to restart the process.
 */

public class OtpLoginAuthenticator extends AbstractUsernameFormAuthenticator
    implements Authenticator
{
    private static final Logger LOG = Logger.getLogger(OtpLoginAuthenticator.class);

    private static final String FTL_ENTER_CODE       = "enter-code.ftl";
    public static final String AUTH_NOTE_USER_EMAIL = "user-email";
    public static final String AUTH_NOTE_OTP = "email-code";
    public static final String AUTH_NOTE_TIMESTAMP  = "timestamp";
    public static final String FORM_ATTRIBUTE_USER_EMAIL  = "userEmail";

    public static final int CODE_TIMEOUT_IN_MINUTES  = 20;
    public static final int CODE_ACTIVATION_DELAY_IN_SECONDS  = 2;


    private SecureCode secureCode;

    public OtpLoginAuthenticator(SecureCode secureCode)
    {
        this.secureCode = secureCode;
    }

    @Override
    public void action(AuthenticationFlowContext context)
    {
        if(LOG.isDebugEnabled()){
            LOG.debugf("Authenticate action OtpLoginAuthenticator");
        }


        //prepareOtpForm(context);
        /* retrieve formData*/
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String codeInput = formData.getFirst("codeInput");

        if (codeInput == null || codeInput.isEmpty()) {
            context.challenge(otpForm(context,"Veuillez renseignez un code"));
            return;
        }

        if (context.getAuthenticationSession().getAuthNote(AUTH_NOTE_OTP) == null) {
            context.challenge(otpFormError(context,"Le code est invalide, veuillez redemander un code"));
            return;
        }

        if (!secureCode.isValid(codeInput, context.getAuthenticationSession().getAuthNote(AUTH_NOTE_OTP),
                               context.getAuthenticationSession().getAuthNote(AUTH_NOTE_TIMESTAMP),
                CODE_TIMEOUT_IN_MINUTES,
                CODE_ACTIVATION_DELAY_IN_SECONDS)) {
            //code validation has failed
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS,otpFormError(context,"Le code n'est pas valide"));
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
        if(LOG.isDebugEnabled()){
            LOG.debugf("Authenticate OtpLoginAuthenticator");
        }

        context.challenge(otpForm(context,null));
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
        if(info !=null){
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
