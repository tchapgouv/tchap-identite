package org.beta.tchap.identite.authenticator;

import static org.mockito.Mockito.mock;

import org.beta.tchap.identite.email.EmailSender;
import org.beta.tchap.identite.utils.SecureCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OtpLoginAuthenticatorTest {
    

    OtpLoginAuthenticator authenticator;

    @BeforeEach
    public void setup() {
        EmailSender emailSender = mock(EmailSender.class); 
        SecureCode secureCode = mock(SecureCode.class); 
        int codeTimeout = 0;
        int mailDelay = 0;
        authenticator = new OtpLoginAuthenticator(secureCode, emailSender, codeTimeout, mailDelay);
    }


    @Test
    public void test(){

    }


}
