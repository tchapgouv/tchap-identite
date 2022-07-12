/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.keycloak.authenticator;

import static org.beta.authentification.keycloak.authenticator.OtpLoginAuthenticator.*;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Response;

import org.beta.authentification.keycloak.utils.LoggingUtilsFactory;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.sessions.AuthenticationSessionModel;

/** Verify that user in login hint is found in users federation (tchap), 
 * request scoped object 
 * */
public class TchapAuthenticator implements Authenticator {

    private static final String FTL_UNAUTHORIZED_USER = "unauthorized-user.ftl";
    private static final Integer MAX_LOGIN_HINTS_OCCURENCE_FOR_ONE_BROWSER = 10;

    private static final Logger LOG = Logger.getLogger(TchapAuthenticator.class);

    public static final String ERROR_UNKNOWN_USER = "unknow user";
    public static final String ERROR_TOO_MANY_REQUESTS = "too many requests";
    public static final String ERROR_MALFORMED_REQUEST = "request is malformed";

    /** Verify that user in login hint is found in users federation (tchap) */
    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticationSessionModel session = context.getAuthenticationSession();

        // retrieve login hint from a standard note injected by oidc
        String loginHint = session.getClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM);

        if (loginHint == null) {
            context.failure(
                    AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR,
                    Response.status(400).build(),
                    ERROR_MALFORMED_REQUEST,
                    ERROR_MALFORMED_REQUEST);
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debugf(
                    "Authenticate login : %s, AuthenticationSession.TabId : %s, ParentSession.Id"
                            + " %s",
                    LoggingUtilsFactory.getInstance().logOrHash(loginHint),
                    context.getAuthenticationSession().getTabId(),
                    context.getAuthenticationSession().getParentSession().getId());
        }

        if (tooManyLoginHints(context)) {
            LOG.warnf(
                    "Authenticate login : %s, parent session has used too many different"
                            + " loginHints",
                    LoggingUtilsFactory.getInstance().logOrHash(loginHint));
            context.failure(
                    AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR,
                    Response.status(400).build(),
                    ERROR_TOO_MANY_REQUESTS,
                    ERROR_TOO_MANY_REQUESTS);

            return;
        }

        // retrieve user from loginHint in keycloak authentication session (attached to browser>tab
        // via cookie)
        UserModel user = getUser(context, loginHint);

        if (user == null) {
            showUnauthorizedUser(context);
            return;
        }
        context.setUser(user);
        context.getAuthenticationSession().setAuthNote(AUTH_NOTE_USER_EMAIL, user.getUsername());
        context.success();
    }

    /**
     * check that occurences login hints have been used from authentication sessions from this
     * browser is not superior than MAX_LOGIN_HINTS_OCCURENCE_FOR_ONE_BROWSER
     *
     * @param context keycloak auth context
     * @return number of different login hints
     */
    private boolean tooManyLoginHints(AuthenticationFlowContext context) {
        Set<String> loginHints = new HashSet<>();
        for (AuthenticationSessionModel session :
                context.getAuthenticationSession()
                        .getParentSession()
                        .getAuthenticationSessions()
                        .values()) {
            String loginHint = session.getAuthNote(AUTH_NOTE_USER_EMAIL);
            if (loginHint != null) {
                loginHints.add(loginHint);
            }
        }
        return loginHints.size() > MAX_LOGIN_HINTS_OCCURENCE_FOR_ONE_BROWSER;
    }

    /**
     * Prepare a error view
     *
     * @param context
     */
    private void showUnauthorizedUser(AuthenticationFlowContext context) {
        context.failure(
                AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR,
                context.form().createForm(FTL_UNAUTHORIZED_USER),
                ERROR_UNKNOWN_USER,
                ERROR_UNKNOWN_USER);
    }

    /**
     * Get user from AUTH_NOTE_USER_EMAIL
     *
     * @param context
     * @return userModel retrieved by the user storage
     */
    private UserModel getUser(AuthenticationFlowContext context, String loginHint) {
        return context.getSession().users().getUserByEmail(context.getRealm(), loginHint);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // NO FORM
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
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}

    @Override
    public void close() {}
}
