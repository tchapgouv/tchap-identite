package org.beta.tchap.identite.authenticator;

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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MockFactory {

    public static class AuthenticationFlowContextBuilder {
        String loginHint;
        String authNote;
        String username;
        boolean temporarilyDisabled;
        List<String> usernames = new ArrayList<>();

        public AuthenticationFlowContextBuilder(){}

        public AuthenticationFlowContextBuilder withLoginHint(String loginHint){
            this.loginHint = loginHint;
            return this;
        }

        public AuthenticationFlowContextBuilder addValidUser(String username){
            this.usernames.add(username);
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

        public AuthenticationFlowContextBuilder addAuthNote(String authNote){
            this.authNote = authNote;
            return this;
        }

        public AuthenticationFlowContext build() {
            AuthenticationFlowContext contextMock = spy(AuthenticationFlowContext.class);
            AuthenticationSessionModel sessionMock = spy(AuthenticationSessionModel.class);
            RootAuthenticationSessionModel rootSessionMock = spy(RootAuthenticationSessionModel.class);
            KeycloakSession keycloakSession = spy(KeycloakSession.class);
            LoginFormsProvider loginFormsProvider = buildLoginFormsProvider();
            RealmModel realmModel = mock(RealmModel.class);
            UserProvider userProviderMock = buildUserProvider(usernames);
            BruteForceProtector bruteForceProtectorMock = buildBruteForceProtector(temporarilyDisabled);
            UserModel userModelMock = buildUserModel(username);

            if(loginHint != null){
                doReturn(loginHint).when(sessionMock).getClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM);
            }

            doReturn(userProviderMock).when(keycloakSession).users();
            doReturn(bruteForceProtectorMock).when(keycloakSession).getProvider(BruteForceProtector.class);

            doReturn(loginFormsProvider).when(contextMock).form();
            doReturn(sessionMock).when(contextMock).getAuthenticationSession();
            doReturn(keycloakSession).when(contextMock).getSession();
            doReturn(realmModel).when(contextMock).getRealm();
            doReturn(userModelMock).when(contextMock).getUser();

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
 
}
