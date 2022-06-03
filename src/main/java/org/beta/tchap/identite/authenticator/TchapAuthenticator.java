package org.beta.tchap.identite.authenticator;

import org.beta.tchap.identite.utils.LoggingUtilsFactory;
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


import java.util.HashSet;
import java.util.Set;

import static org.beta.tchap.identite.authenticator.OtpLoginAuthenticator.*;

public class TchapAuthenticator implements Authenticator {

    private static final String FTL_UNAUTHORIZED_USER       = "unauthorized-user.ftl";
    private static final Integer MAX_LOGIN_HINTS_OCCURENCE_FOR_ONE_BROWSER = 10;

    private static final Logger LOG = Logger.getLogger(TchapAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticationSessionModel session  = context.getAuthenticationSession();
        String loginHint = session.getClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM);

        if(loginHint == null){
            context.failure(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR,
                    Response.status(400).build(), "request is malformed", "request is malformed" );
            return;
        }

        if(LOG.isDebugEnabled()){LOG.debugf("Authenticate login : %s, AuthenticationSession.TabId : %s, ParentSession.Id %s",
                    LoggingUtilsFactory.getInstance().logOrHash(loginHint),
                    context.getAuthenticationSession().getTabId(),
                    context.getAuthenticationSession().getParentSession().getId());
        }

        if(tooManyLoginHints(context)){
            LOG.warnf("Authenticate login : %s, parent session has used too many different loginHints",
                    LoggingUtilsFactory.getInstance().logOrHash(loginHint));
                    context.failure(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR,
                    Response.status(400).build(), "too many requests", "request is too many requests" );

            return;
        }

        //set loginHint in keycloak authentication session (attached to browser>tab via cookie)
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE_USER_EMAIL, loginHint);
        UserModel user = getUser(context);

        if (user == null) {
            showUnauthorizedUser(context);
            return;
        }

        context.success();
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

    


    private void showUnauthorizedUser(AuthenticationFlowContext context){
        context.failure(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR,  context.form()
                .createForm(FTL_UNAUTHORIZED_USER)
                , "unknown user", "unknow user");
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
