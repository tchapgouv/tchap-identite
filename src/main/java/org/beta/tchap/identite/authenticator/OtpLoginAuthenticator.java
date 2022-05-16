package org.beta.tchap.identite.authenticator;

import org.beta.tchap.identite.email.EmailSender;
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
    public static final String AUTH_NOTE_EMAIL_CODE = "email-code";
    public static final String AUTH_NOTE_TIMESTAMP  = "timestamp";

    private EmailSender emailSender;
    private SecureCode secureCode;

    public OtpLoginAuthenticator(EmailSender emailSender, SecureCode secureCode)
    {
        this.emailSender = emailSender;
        this.secureCode = secureCode;
    }

    @Override
    public void action(AuthenticationFlowContext context)
    {

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        context.challenge(context.form().createForm(FTL_ENTER_CODE));

        //String email = formData.getFirst("email");
        String codeInput = formData.getFirst("codeInput");
/*
        if (email != null) {

            context.getAuthenticationSession().setAuthNote(AUTH_NOTE_USER_EMAIL, email);
            UserModel user = getUser(context);

            if (user == null || !user.isEnabled()) {
                context.failure(AuthenticationFlowError.INVALID_USER);
            }
            else {
                generateAndSendCode(context);
            }

        }
        else */

        if (codeInput != null && context.getAuthenticationSession().getAuthNote(AUTH_NOTE_EMAIL_CODE) != null) {

            if (secureCode.isValid(codeInput, context.getAuthenticationSession().getAuthNote(AUTH_NOTE_EMAIL_CODE),
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
    /*
    private String redirectBasedOnProvidedUserInfo(AuthenticationFlowContext context)
    {
        String redirect;
        try {
            context.getAuthenticationSession().setAuthNote(AUTH_NOTE_USER_EMAIL, context.getUser().getEmail());
            generateAndSendCode(context);
            redirect = FTL_ENTER_CODE;
        } catch (NullPointerException e) {
            redirect = FTL_ENTER_EMAIL;
        }

        return redirect;
    }*/

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
