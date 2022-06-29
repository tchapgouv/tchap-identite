package org.beta.tchap.identite.authenticator;

import org.beta.tchap.identite.bot.BotSender;
import org.beta.tchap.identite.email.EmailSender;
import org.beta.tchap.identite.utils.SecureCode;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import javax.ws.rs.core.Response;

public class OtpLoginAuthenticatorTest {
    

    OtpLoginAuthenticator authenticator;
    
    @Mock EmailSender emailSender; 
    @Mock SecureCode secureCode;
    @Mock BotSender botSender;
    int codeTimeout = 0;
    int mailDelay = 0;

    @Captor ArgumentCaptor<Response> responseCaptor;

    @BeforeEach
    public void setup() {
        authenticator = new OtpLoginAuthenticator(secureCode, emailSender, codeTimeout, mailDelay,botSender);
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
