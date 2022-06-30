package org.beta.tchap.identite.authenticator;

import org.beta.tchap.identite.bot.BotSender;
import org.beta.tchap.identite.email.EmailSender;
import org.beta.tchap.identite.utils.SecureCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OtpLoginAuthenticatorTest {

    OtpLoginAuthenticator authenticator;
    
    @Mock EmailSender emailSender; 
    @Mock SecureCode secureCode;
    @Mock BotSender botSender;
    int codeTimeout = 0;
    int mailDelay = 0;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        authenticator = new OtpLoginAuthenticator(secureCode, emailSender, codeTimeout, mailDelay, botSender);
    }

    @Nested
    class FailedAuthenticateFlowTest {

        @Test
        public void authenticate_shouldFail_with_no_challenge_and_unknown_user_flow_error(){
            AuthenticationFlowContext context =  new MockFactory.AuthenticationFlowContextBuilder().build();

            authenticator.authenticate(context);

            verify(context).failure(AuthenticationFlowError.UNKNOWN_USER);
            verify(context,times(0)).failure(any(),any());
            verify(context,times(0)).failure(any(),any(),any(),any());
            verify(context,times(0)).failureChallenge(any(),any());
            verify(context,times(0)).success();
            verify(context,times(0)).challenge(any());
        }
    }

    @Nested
    class ContinueAuthenticateFlowTest {

        @Test
        public void authenticate_should_continue_without_failure(){
            AuthenticationFlowContext context =  new MockFactory.AuthenticationFlowContextBuilder()
                    .withUser("myUserId")
                    .withTemporarilyDisabled(false)
                    .build();

            doReturn("bbb-aaa").when(secureCode).generateCode(anyInt());

            authenticator.authenticate(context);

            verify(context,times(0)).failure(any());
            verify(context,times(0)).failure(any(),any());
            verify(context,times(0)).failure(any(),any(),any(),any());
            verify(context,times(0)).failureChallenge(any(),any());
            verify(context,times(0)).success();
            // regular otp form
            verify(context,times(1)).challenge(any());
        }

        @Test
        public void authenticate_should_continue_without_failure_for_disabled_users(){
            AuthenticationFlowContext context =  new MockFactory.AuthenticationFlowContextBuilder()
                    .withUser("myUserId")
                    .withTemporarilyDisabled(true)
                    .build();

            authenticator.authenticate(context);

            verify(context,times(0)).failure(any());
            verify(context,times(0)).failure(any(),any());
            verify(context,times(0)).failure(any(),any(),any(),any());
            verify(context,times(0)).failureChallenge(any(),any());
            verify(context,times(0)).success();
            // regular otp form
            verify(context,times(1)).challenge(any());

        }

        @Test
        public void authenticate_should_continue_with_error_challenge_for_email_not_sent(){
            AuthenticationFlowContext context =  new MockFactory.AuthenticationFlowContextBuilder()
                    .withUser("myUserId")
                    .withTemporarilyDisabled(false)
                    .build();

            String code = "bbb-aaa";
            doReturn(code).when(secureCode).generateCode(anyInt());
            doReturn(false).when(emailSender).sendEmail(any(),any(),any(),eq(code),eq(String.valueOf(codeTimeout)));

            authenticator.authenticate(context);

            verify(context,times(0)).failure(any());
            verify(context,times(0)).failure(any(),any());
            verify(context,times(0)).failure(any(),any(),any(),any());
            verify(context,times(0)).failureChallenge(any(),any());
            verify(context,times(0)).success();
            // error otp form
            verify(context,times(1)).challenge(any());
        }

    }




}
