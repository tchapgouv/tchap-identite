package org.beta.tchap.identite.authenticator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.RootAuthenticationSessionModel;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class MockFactory {

    public static class AuthenticationFlowContextBuilder {
        String loginHint;
        String authNote;
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

        public AuthenticationFlowContextBuilder addAuthNote(String authNote){
            this.authNote = authNote;
            return this;
        }

        public AuthenticationFlowContext build(){
            AuthenticationFlowContext contextMock = spy(AuthenticationFlowContext.class);
            AuthenticationSessionModel sessionMock = spy(AuthenticationSessionModel.class);
            RootAuthenticationSessionModel rootSessionMock = spy(RootAuthenticationSessionModel.class);
            KeycloakSession keycloakSession = spy(KeycloakSession.class);
            LoginFormsProvider loginFormsProvider = spy(LoginFormsProvider.class);
            RealmModel realmModel = mock(RealmModel.class);

            if(loginHint != null){
                doReturn(loginHint).when(sessionMock).getClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM);
            }

            
            UserProvider userProviderMock = buildUserProvider(usernames);
            doReturn(userProviderMock).when(keycloakSession).users();
        
            
            doReturn(loginFormsProvider).when(contextMock).form();
            doReturn(sessionMock).when(contextMock).getAuthenticationSession();
            doReturn(keycloakSession).when(contextMock).getSession();
            doReturn(realmModel).when(contextMock).getRealm();
            
            doReturn(rootSessionMock).when(sessionMock).getParentSession();

            return contextMock;
        }

    }

    static UserModel buildUserModel(String email){
        UserModel userMock = mock(UserModel.class);
        doReturn(email).when(userMock).getEmail();
        doReturn(email).when(userMock).getUsername();
        return userMock;
    }

    static UserProvider buildUserProvider(List<String> usernames){
        UserProvider userProviderMock = spy(UserProvider.class);
        //final List<String> validUsernames = Arrays.asList(usernames);
        
         doAnswer(new Answer<UserModel>(){
            @Override
            public UserModel answer(InvocationOnMock invocation) throws Throwable {
                //Object realm = invocation.getArguments()[0];
                String usernameParam = (String) invocation.getArguments()[1];
                //System.out.printf("Invocation Arguments : %s %s", realm, usernameParam);
                if(usernames.contains(usernameParam)){
                    return buildUserModel(usernameParam);
                }
                return null;
            }}).when(userProviderMock).getUserByEmail(any(RealmModel.class), anyString());    
                    
        return userProviderMock; 
    }
 
}
