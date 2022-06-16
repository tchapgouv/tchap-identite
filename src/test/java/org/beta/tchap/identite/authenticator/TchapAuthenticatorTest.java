package org.beta.tchap.identite.authenticator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import javax.ws.rs.core.Response;

import org.apache.log4j.BasicConfigurator;
import org.beta.tchap.TestSuiteUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
public class TchapAuthenticatorTest {
    

    TchapAuthenticator authenticator = new TchapAuthenticator();
    
    @Captor ArgumentCaptor<Response> responseCaptor;
    @Captor ArgumentCaptor<String> userErrorCaptor;
    @Captor ArgumentCaptor<String> authNote;

    final String username = "userA";

    @BeforeEach
    public void setup() {
        BasicConfigurator.configure();
    }


    @Test
    public void withoutLoginHint_shouldFail_return400(){
        AuthenticationFlowContext context =  new MockFactory.AuthenticationFlowContextBuilder().build();
        authenticator.authenticate(context);
        verify(context).failure(any(),responseCaptor.capture(),any(),any());
        assertEquals(400,responseCaptor.getValue().getStatus());
    }

    @Test
    public void whenUserNotExists_shouldShowErrorMessage(){
        AuthenticationFlowContext context =  new MockFactory.AuthenticationFlowContextBuilder()
            .withLoginHint("userA")
            .build();

        authenticator.authenticate(context);
        verify(context).failure(any(),any(),any(),userErrorCaptor.capture());
        //todo check form
        assertEquals(TchapAuthenticator.ERROR_UNKNOWN_USER,userErrorCaptor.getValue());

    }
    
    @Test
    public void whenUserExists_shouldSetAuthNote_andSuccess(){
        AuthenticationFlowContext context =  
            new MockFactory.AuthenticationFlowContextBuilder()
                .withLoginHint(username)
                .addValidUser(username)
                .build();
        AuthenticationSessionModel session = context.getAuthenticationSession();

        authenticator.authenticate(context);
        verify(context).success();
        verify(session).setAuthNote(eq(OtpLoginAuthenticator.AUTH_NOTE_USER_EMAIL), authNote.capture());
        assertEquals(username, authNote.getValue());
    }

    //todo too many login hints

}
