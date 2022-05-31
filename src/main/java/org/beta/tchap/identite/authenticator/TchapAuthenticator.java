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

import static org.beta.tchap.identite.authenticator.OtpLoginAuthenticator.*;

public class TchapAuthenticator implements Authenticator {

    private final SecureCode secureCode;
    private final EmailSender emailSender;
    private static final String FTL_UNAUTHORIZED_USER       = "unauthorized-user.ftl";

    private static final Logger LOG = Logger.getLogger(TchapAuthenticator.class);

    TchapAuthenticator(EmailSender emailSender, SecureCode secureCode){
        this.secureCode = secureCode;
        this.emailSender = emailSender;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticationSessionModel session  = context.getAuthenticationSession();
        String loginHint = session.getClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM);

        if(loginHint ==null){
            context.failure(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR,
                    Response.status(400).build(), "request is malformed", "request is malformed" );
            return;
        }
        if(LOG.isDebugEnabled()){
            LOG.debugf("Authenticate login : %s", LoggingUtilsFactory.getInstance().logOrHash(loginHint));
        }
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE_USER_EMAIL, loginHint);
        UserModel user = getUser(context);

        if (user == null) {
            showUnautorhizedUser(context);
            return;
        }

        //user has been found
        generateAndSendCode(context);
        context.success();
    }

    private void showUnautorhizedUser(AuthenticationFlowContext context){
        String message = "Cet email ne correspond pas à une agence de l'État. Si vous appartenez à un service de l'État mais votre email n'est pas reconnu par AudioConf, contactez-nous pour que nous le rajoutions!";
           /* context.failure(AuthenticationFlowError.UNKNOWN_USER, Response.status(401).build(),
                    "user was not found in Tchap", message );
            */
        context.failure(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR,  context.form()
                .createForm(FTL_UNAUTHORIZED_USER)
                , "unknown user", "unknow user");



    }

    private void generateAndSendCode(AuthenticationFlowContext context)
    {
        String code = secureCode.generateCode(6);
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE_OTP, code);
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE_TIMESTAMP,
                Long.toString(System.currentTimeMillis()));

        String friendlyCode = secureCode.makeCodeUserFriendly(code);
        if(LOG.isDebugEnabled()){
            LOG.debugf("Sending OTP : %s", LoggingUtilsFactory.getInstance().logOrHide(friendlyCode));
        }
        emailSender.sendEmail(context.getSession(), context.getRealm(),
                              getUser(context), friendlyCode);


        /*
         * TODO: SEND CODE TO TCHAP ALSO
         */
    }

    private UserModel getUser(AuthenticationFlowContext context)
    {
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
