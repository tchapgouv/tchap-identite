package org.beta.tchap.identite.authenticator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.ArgumentCaptor;

public class TchapAuthenticatorTest {
    

    TchapAuthenticator authenticator;
    ArgumentCaptor<Response> responseCaptor;
    ArgumentCaptor<String> userErrorCaptor;
    ArgumentCaptor<String> authNote;

    final String username = "userA";

    @BeforeEach
    public void setup() {
        authenticator = new TchapAuthenticator();
        responseCaptor = ArgumentCaptor.forClass(Response.class);
        userErrorCaptor = ArgumentCaptor.forClass(String.class);
        authNote = ArgumentCaptor.forClass(String.class);
    }


    @Test
    public void withoutLoginHint_shouldFail_return400(){
        AuthenticationFlowContext context =  new MockFactory.AuthenticationFlowContextBuilder().build();
        authenticator.authenticate(context);
        verify(context).failure(any(),responseCaptor.capture(),any(),any());
        assertEquals(400,responseCaptor.getValue().getStatus());
    }

    @Test
    public void withLoginHint_userNotExists_shouldShowErrorMessage(){
        AuthenticationFlowContext context =  new MockFactory.AuthenticationFlowContextBuilder()
            .withLoginHint("userA")
            .withValidUser("")
            .build();

        authenticator.authenticate(context);
        verify(context).failure(any(),any(),any(),userErrorCaptor.capture());
        //todo check form
        assertEquals(TchapAuthenticator.ERROR_UNKNOWN_USER,userErrorCaptor.getValue());

    }
    
    @Test
    public void withUserExists_shouldSetAuthNote_andSuccess(){
        AuthenticationFlowContext context =  
            new MockFactory.AuthenticationFlowContextBuilder()
                .withLoginHint(username)
                .withValidUser(username)
                .build();
        AuthenticationSessionModel session = context.getAuthenticationSession();

        authenticator.authenticate(context);
        verify(context).success();
        verify(session).setAuthNote(eq(OtpLoginAuthenticator.AUTH_NOTE_USER_EMAIL), authNote.capture());
        assertEquals(username, authNote.getValue());
    }

    //todo too many login hints

}
