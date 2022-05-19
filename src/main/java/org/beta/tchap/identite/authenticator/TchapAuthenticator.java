package org.beta.tchap.identite.authenticator;

import org.beta.tchap.identite.email.EmailSender;
import org.beta.tchap.identite.matrix.rest.MatrixService;
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

import static org.beta.tchap.identite.authenticator.OtpLoginAuthenticator.*;

public class TchapAuthenticator implements Authenticator {

    private final SecureCode secureCode;
    private final EmailSender emailSender;

    private static final Logger LOG = Logger.getLogger(TchapAuthenticator.class);

    TchapAuthenticator(EmailSender emailSender, SecureCode secureCode){
        this.secureCode = secureCode;
        this.emailSender = emailSender;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticationSessionModel session  = context.getAuthenticationSession();
        String loginHint = session.getClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM);

        if(loginHint !=null){
            LOG.infof("Authenticate login : %s", loginHint);
            context.getAuthenticationSession().setAuthNote(AUTH_NOTE_USER_EMAIL, loginHint);
            UserModel user = getUser(context);

            if (user == null || !user.isEnabled()) {
                context.failure(AuthenticationFlowError.INVALID_USER);
            } else {
                generateAndSendCode(context);
                context.success();
            }
        }else{
            context.failure(AuthenticationFlowError.INVALID_USER);
        }
    }

    private void generateAndSendCode(AuthenticationFlowContext context)
    {
        String code = secureCode.generateCode(6);
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE_OTP, code);
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE_TIMESTAMP,
                Long.toString(System.currentTimeMillis()));

        String friendlyCode = secureCode.makeCodeUserFriendly(code);
        LOG.infof("Sending OTP : %s", friendlyCode);

        emailSender.sendEmail(context.getSession(), context.getRealm(),
                              getUser(context), friendlyCode);


        /*
         * SEND DM TCHAP OTP IF TCHAP ACCOUNT
         */
    }

    private UserModel getUser(AuthenticationFlowContext context)
    {
        return context.getSession().users().getUserByEmail(context.getRealm(), context.getAuthenticationSession().getAuthNote(AUTH_NOTE_USER_EMAIL));
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
