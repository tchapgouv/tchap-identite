/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.keycloak.authenticator;

import static org.beta.authentification.keycloak.user.TchapUserStorage.ATTRIBUTE_HOMESERVER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.services.managers.BruteForceProtector;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.RootAuthenticationSessionModel;
import org.mockito.stubbing.Answer;

public class MockFactory {

    public static class AuthenticationFlowContextBuilder {
        String loginHint;
        String username;
        String homeServer;
        String codeInput;
        boolean temporarilyDisabled;
        List<String> usernames = new ArrayList<>();
        Map<String, String> authNoteMap = new HashMap<>();

        public AuthenticationFlowContextBuilder() {}

        public AuthenticationFlowContextBuilder withLoginHint(String loginHint) {
            this.loginHint = loginHint;
            return this;
        }

        public AuthenticationFlowContextBuilder addValidUser(String username) {
            this.usernames.add(username);
            return this;
        }

        public AuthenticationFlowContextBuilder withCodeInput(String codeInput) {
            this.codeInput = codeInput;
            return this;
        }

        public AuthenticationFlowContextBuilder withUser(String username) {
            this.username = username;
            return this;
        }

        public AuthenticationFlowContextBuilder withHomeServer(String homeServer) {
            this.homeServer = homeServer;
            return this;
        }

        public AuthenticationFlowContextBuilder withTemporarilyDisabled(
                boolean temporarilyDisabled) {
            this.temporarilyDisabled = temporarilyDisabled;
            return this;
        }

        public AuthenticationFlowContextBuilder addAuthNote(
                String authNoteKey, String authNoteValue) {
            this.authNoteMap.put(authNoteKey, authNoteValue);
            return this;
        }

        public AuthenticationFlowContext build() {
            AuthenticationFlowContext contextMock = spy(AuthenticationFlowContext.class);
            AuthenticationSessionModel sessionMock =
                    buildAuthenticationSessionModel(loginHint, authNoteMap);
            RootAuthenticationSessionModel rootSessionMock =
                    spy(RootAuthenticationSessionModel.class);
            KeycloakSession keycloakSession = spy(KeycloakSession.class);
            LoginFormsProvider loginFormsProvider = buildLoginFormsProvider();
            RealmModel realmModel = mock(RealmModel.class);
            UserProvider userProviderMock = buildUserProvider(usernames, homeServer);
            BruteForceProtector bruteForceProtectorMock =
                    buildBruteForceProtector(temporarilyDisabled);
            UserModel userModelMock = buildUserModel(username, homeServer);
            HttpRequest httpRequestMock = buildHttpRequest(codeInput);
//            context.getAuthenticationSession().getClient().getName()
            // keycloak session
            doReturn(userProviderMock).when(keycloakSession).users();
            doReturn(bruteForceProtectorMock)
                    .when(keycloakSession)
                    .getProvider(BruteForceProtector.class);

            // context
            doReturn(loginFormsProvider).when(contextMock).form();
            doReturn(sessionMock).when(contextMock).getAuthenticationSession();
            doReturn(keycloakSession).when(contextMock).getSession();
            doReturn(realmModel).when(contextMock).getRealm();
            doReturn(userModelMock).when(contextMock).getUser();
            doReturn(httpRequestMock).when(contextMock).getHttpRequest();

            // authentication session
            doReturn(rootSessionMock).when(sessionMock).getParentSession();

            return contextMock;
        }
    }

    static UserModel buildUserModel(String email, String homeServer) {
        if (email == null) {
            return null;
        }
        UserModel userMock = mock(UserModel.class);
        doReturn(email).when(userMock).getEmail();
        doReturn(email).when(userMock).getUsername();
        doReturn(homeServer).when(userMock).getFirstAttribute(ATTRIBUTE_HOMESERVER);
        return userMock;
    }

    static UserProvider buildUserProvider(List<String> usernames, String homeServer) {
        UserProvider userProviderMock = spy(UserProvider.class);
        // final List<String> validUsernames = Arrays.asList(usernames);

        doAnswer(
                        (Answer<UserModel>)
                                invocation -> {
                                    // Object realm = invocation.getArguments()[0];
                                    String usernameParam = (String) invocation.getArguments()[1];
                                    // System.out.printf("Invocation Arguments : %s %s", realm,
                                    // usernameParam);
                                    if (usernames.contains(usernameParam)) {
                                        return buildUserModel(usernameParam, homeServer);
                                    }
                                    return null;
                                })
                .when(userProviderMock)
                .getUserByEmail(any(RealmModel.class), anyString());

        return userProviderMock;
    }

    static BruteForceProtector buildBruteForceProtector(boolean temporarilyDisabled) {
        BruteForceProtector bruteForceProtectorMock = mock(BruteForceProtector.class);
        doReturn(temporarilyDisabled)
                .when(bruteForceProtectorMock)
                .isTemporarilyDisabled(
                        any(KeycloakSession.class), any(RealmModel.class), any(UserModel.class));
        return bruteForceProtectorMock;
    }

    static LoginFormsProvider buildLoginFormsProvider() {
        LoginFormsProvider loginFormsProviderMock = spy(LoginFormsProvider.class);
        doReturn(loginFormsProviderMock)
                .when(loginFormsProviderMock)
                .setAttribute(anyString(), any());
        doReturn(loginFormsProviderMock).when(loginFormsProviderMock).setError(anyString(), any());
        return loginFormsProviderMock;
    }

    static HttpRequest buildHttpRequest(String codeInput) {
        HttpRequest httpRequestMock = mock(HttpRequest.class);
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("codeInput", codeInput);
        doReturn(formData).when(httpRequestMock).getDecodedFormParameters();
        return httpRequestMock;
    }

    static AuthenticationSessionModel buildAuthenticationSessionModel(
            String loginHint, Map<String, String> authNoteMap) {
        AuthenticationSessionModel sessionMock = spy(AuthenticationSessionModel.class);

        if (loginHint != null) {
            doReturn(loginHint).when(sessionMock).getClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM);
        }

        doAnswer(
                        (Answer<String>)
                                invocation -> {
                                    String authNoteKeyParam = (String) invocation.getArguments()[0];
                                    return authNoteMap.get(authNoteKeyParam);
                                })
                .when(sessionMock)
                .getAuthNote(anyString());


        ClientModel client = mock(ClientModel.class);
        doReturn("Audioconf").when(client).getName();
        doReturn(client).when(sessionMock).getClient();

        return sessionMock;
    }
}
