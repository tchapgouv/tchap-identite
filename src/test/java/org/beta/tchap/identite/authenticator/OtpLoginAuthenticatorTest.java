package org.beta.tchap.identite.authenticator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import javax.ws.rs.core.Response;

import org.beta.tchap.identite.email.EmailSender;
import org.beta.tchap.identite.utils.SecureCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

public class OtpLoginAuthenticatorTest {
    

    OtpLoginAuthenticator authenticator;
    
    @Mock EmailSender emailSender; 
    @Mock SecureCode secureCode; 
    int codeTimeout = 0;
    int mailDelay = 0;

    @Captor ArgumentCaptor<Response> responseCaptor;

    @BeforeEach
    public void setup() {
        authenticator = new OtpLoginAuthenticator(secureCode, emailSender, codeTimeout, mailDelay);
    }

    //TODO, it is not implemented like this
    /*
    @Test
    public void withoutLoginHint_shouldFail_return400(){
        
        AuthenticationFlowContext context =  new MockFactory.AuthenticationFlowContextBuilder().build();
        authenticator.authenticate(context);
        verify(context).failure(any(),responseCaptor.capture(),any(),any());
        assertEquals(400,responseCaptor.getValue().getStatus());
    }
    */


}
