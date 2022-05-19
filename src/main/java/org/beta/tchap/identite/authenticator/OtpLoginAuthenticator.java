package org.beta.tchap.identite.authenticator;

import org.beta.tchap.identite.utils.SecureCode;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;

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

    private SecureCode secureCode;

    public OtpLoginAuthenticator(SecureCode secureCode)
    {
        this.secureCode = secureCode;
    }

    @Override
    public void action(AuthenticationFlowContext context)
    {
        LOG.debugf("Authenticate user by otp");

        String userEmail = context.getAuthenticationSession().getAuthNote(AUTH_NOTE_USER_EMAIL);

        /* if userEmail is not set in the authentication session, fails */
        if(userEmail==null || userEmail.isEmpty()){
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS,
                    context.form().createForm(redirectBasedOnProvidedUserInfo(context)));
            return;
        }

        /* display otp form*/
        context.challenge(context.form()
                .setAttribute(FORM_ATTRIBUTE_USER_EMAIL, userEmail)
                .createForm(FTL_ENTER_CODE));

        /* retrieve formData*/
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String codeInput = formData.getFirst("codeInput");

        if (codeInput != null && context.getAuthenticationSession().getAuthNote(AUTH_NOTE_OTP) != null) {

            if (secureCode.isValid(codeInput, context.getAuthenticationSession().getAuthNote(AUTH_NOTE_OTP),
                                   context.getAuthenticationSession().getAuthNote(AUTH_NOTE_TIMESTAMP), 20, 2)) {
                context.setUser(getUser(context));
                context.success();
            }
            else {
                context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS,
                                         context.form().createForm(redirectBasedOnProvidedUserInfo(context)));
            }
        }
        else {
            context.challenge(context.form().createForm(redirectBasedOnProvidedUserInfo(context)));
        }
    }

    private UserModel getUser(AuthenticationFlowContext context)
    {
        return context.getSession().users().getUserByEmail(context.getAuthenticationSession().getAuthNote(AUTH_NOTE_USER_EMAIL), context.getRealm());
    }

    @Override
    public void authenticate(AuthenticationFlowContext context)
    {
        context.challenge(context.form().createForm(redirectBasedOnProvidedUserInfo(context)));
    }

    /**
     * checks if there already is a user attached to the authentication flow to avoid asking for
     * identity more than once
     */
    private String redirectBasedOnProvidedUserInfo(AuthenticationFlowContext context)
    {
        return FTL_ENTER_CODE;
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
