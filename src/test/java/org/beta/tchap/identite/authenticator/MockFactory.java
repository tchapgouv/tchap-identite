/*
 * Copyright (c) 2022. DINUM
 */

package org.beta.tchap.identite.authenticator;

import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.services.managers.BruteForceProtector;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.RootAuthenticationSessionModel;
import org.mockito.stubbing.Answer;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MockFactory {

    public static class AuthenticationFlowContextBuilder {
        String loginHint;
        String username;
        String codeInput;
        boolean temporarilyDisabled;
        List<String> usernames = new ArrayList<>();
        Map<String,String> authNoteMap = new HashMap<>();

        public AuthenticationFlowContextBuilder(){}

        public AuthenticationFlowContextBuilder withLoginHint(String loginHint){
            this.loginHint = loginHint;
            return this;
        }

        public AuthenticationFlowContextBuilder addValidUser(String username){
            this.usernames.add(username);
            return this;
        }

        public AuthenticationFlowContextBuilder withCodeInput(String codeInput){
            this.codeInput = codeInput;
            return this;
        }

        public AuthenticationFlowContextBuilder withUser(String username){
            this.username = username;
            return this;
        }

        public AuthenticationFlowContextBuilder withTemporarilyDisabled(boolean temporarilyDisabled){
            this.temporarilyDisabled = temporarilyDisabled;
            return this;
        }

        public AuthenticationFlowContextBuilder addAuthNote(String authNoteKey,String authNoteValue){
            this.authNoteMap.put(authNoteKey,authNoteValue);
            return this;
        }

        public AuthenticationFlowContext build() {
            AuthenticationFlowContext contextMock = spy(AuthenticationFlowContext.class);
            AuthenticationSessionModel sessionMock = buildAuthenticationSessionModel(loginHint,authNoteMap);
            RootAuthenticationSessionModel rootSessionMock = spy(RootAuthenticationSessionModel.class);
            KeycloakSession keycloakSession = spy(KeycloakSession.class);
            LoginFormsProvider loginFormsProvider = buildLoginFormsProvider();
            RealmModel realmModel = mock(RealmModel.class);
            UserProvider userProviderMock = buildUserProvider(usernames);
            BruteForceProtector bruteForceProtectorMock = buildBruteForceProtector(temporarilyDisabled);
            UserModel userModelMock = buildUserModel(username);
            HttpRequest httpRequestMock = buildHttpRequest(codeInput);

            // keycloak session
            doReturn(userProviderMock).when(keycloakSession).users();
            doReturn(bruteForceProtectorMock).when(keycloakSession).getProvider(BruteForceProtector.class);

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

    static UserModel buildUserModel(String email){
        if (email == null){
            return null;
        }
        UserModel userMock = mock(UserModel.class);
        doReturn(email).when(userMock).getEmail();
        doReturn(email).when(userMock).getUsername();
        return userMock;
    }

    static UserProvider buildUserProvider(List<String> usernames){
        UserProvider userProviderMock = spy(UserProvider.class);
        //final List<String> validUsernames = Arrays.asList(usernames);

         doAnswer((Answer<UserModel>) invocation -> {
             //Object realm = invocation.getArguments()[0];
             String usernameParam = (String) invocation.getArguments()[1];
             //System.out.printf("Invocation Arguments : %s %s", realm, usernameParam);
             if(usernames.contains(usernameParam)){
                 return buildUserModel(usernameParam);
             }
             return null;
         }).when(userProviderMock).getUserByEmail(any(RealmModel.class), anyString());

        return userProviderMock;
    }

    static BruteForceProtector buildBruteForceProtector(boolean temporarilyDisabled){
        BruteForceProtector bruteForceProtectorMock = mock(BruteForceProtector.class);
        doReturn(temporarilyDisabled)
                .when(bruteForceProtectorMock)
                .isTemporarilyDisabled(any(KeycloakSession.class),any(RealmModel.class), any(UserModel.class));
        return bruteForceProtectorMock;
    }

    static LoginFormsProvider buildLoginFormsProvider(){
        LoginFormsProvider loginFormsProviderMock = spy(LoginFormsProvider.class);
        doReturn(loginFormsProviderMock).when(loginFormsProviderMock).setAttribute(anyString(),any());
        doReturn(loginFormsProviderMock).when(loginFormsProviderMock).setError(anyString(),any());
        return loginFormsProviderMock;
    }

    static HttpRequest buildHttpRequest(String codeInput){
        HttpRequest httpRequestMock = mock(HttpRequest.class);
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("codeInput",codeInput);
        doReturn(formData).when(httpRequestMock).getDecodedFormParameters();
        return httpRequestMock;
    }


    static AuthenticationSessionModel buildAuthenticationSessionModel(String loginHint, Map<String,String> authNoteMap){
        AuthenticationSessionModel sessionMock = spy(AuthenticationSessionModel.class);

        if(loginHint != null){
            doReturn(loginHint).when(sessionMock).getClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM);
        }

        doAnswer((Answer<String>) invocation -> {
            String authNoteKeyParam = (String) invocation.getArguments()[0];
            return authNoteMap.get(authNoteKeyParam);
        }).when(sessionMock).getAuthNote(anyString());

        return sessionMock;
    }

}
