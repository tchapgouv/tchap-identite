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

import static org.beta.tchap.identite.authenticator.OtpLoginAuthenticator.AUTH_NOTE_OTP;
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
    class AuthenticateFlowTest {

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


    @Nested
    class ActionFlowTest {

        @Test
        public void action_should_fail_with_invalid_credential_for_disabled_users(){
            AuthenticationFlowContext context =  new MockFactory.AuthenticationFlowContextBuilder()
                    .withUser("myUserId")
                    .withTemporarilyDisabled(true)
                    .build();

            authenticator.action(context);

            verify(context,times(0)).failure(any());
            verify(context,times(0)).failure(any(),any());
            verify(context,times(0)).failure(any(),any(),any(),any());
            // show error otp form: "error.invalid.code"
            verify(context,times(1)).failureChallenge(eq(AuthenticationFlowError.INVALID_CREDENTIALS),any());
            verify(context,times(0)).success();
            verify(context,times(0)).challenge(any());
            verify(secureCode,times(0)).isValid(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyInt(),
                    anyInt());
        }

        @Test
        public void action_should_show_retry_form_with_empty_code(){
            AuthenticationFlowContext context =  new MockFactory.AuthenticationFlowContextBuilder()
                    .withUser("myUserId")
                    .withTemporarilyDisabled(false)
                    .withCodeInput("")
                    .build();

            authenticator.action(context);

            verify(context,times(0)).failure(any());
            verify(context,times(0)).failure(any(),any());
            verify(context,times(0)).failure(any(),any(),any(),any());
            verify(context,times(0)).failureChallenge(any(),any());
            verify(context,times(0)).success();
            // show regular otp form: "info.input.code"
            verify(context,times(1)).challenge(any());
            verify(secureCode,times(0)).isValid(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyInt(),
                    anyInt());
        }

        @Test
        public void action_should_show_resend_code_form_with_no_code_in_session(){
            String codeInput = "ccc-ddd";
            AuthenticationFlowContext context =  new MockFactory.AuthenticationFlowContextBuilder()
                    .withUser("myUserId")
                    .withTemporarilyDisabled(false)
                    .withCodeInput(codeInput)
                    .build();

            authenticator.action(context);

            verify(context,times(0)).failure(any());
            verify(context,times(0)).failure(any(),any());
            verify(context,times(0)).failure(any(),any(),any(),any());
            verify(context,times(0)).failureChallenge(any(),any());
            verify(context,times(0)).success();
            // show error otp form: "error.invalid.code.in.session"
            verify(context,times(1)).challenge(any());
            verify(secureCode,times(0)).isValid(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyInt(),
                    anyInt());
        }

        @Test
        public void action_should_show_invalid_code_form_with_wrong_code_in_session(){
            String codeInput = "ccc-ddd";
            String codeInSession = "ccc-zzz";
            AuthenticationFlowContext context =  new MockFactory.AuthenticationFlowContextBuilder()
                    .withUser("myUserId")
                    .withTemporarilyDisabled(false)
                    .withCodeInput(codeInput)
                    .addAuthNote(AUTH_NOTE_OTP, codeInSession)
                    .build();

            doReturn(false).when(secureCode).isValid(
                    eq(codeInput),
                    eq(codeInSession),
                    anyString(),
                    eq(codeTimeout),
                    anyInt());

            authenticator.action(context);

            verify(context,times(0)).failure(any());
            verify(context,times(0)).failure(any(),any());
            verify(context,times(0)).failure(any(),any(),any(),any());
            // show error otp form: "error.invalid.code"
            verify(context,times(1)).failureChallenge(any(),any());
            verify(context,times(0)).success();
            verify(context,times(0)).challenge(any());
            verify(secureCode,times(0)).isValid(
                    eq(codeInput),
                    eq(codeInSession),
                    anyString(),
                    eq(codeTimeout),
                    anyInt());
        }


        @Test
        public void action_should_succeed(){
            String codeInput = "ccc-ddd";
            String codeInSession = codeInput;
            AuthenticationFlowContext context =  new MockFactory.AuthenticationFlowContextBuilder()
                    .withUser("myUserId")
                    .withTemporarilyDisabled(false)
                    .withCodeInput(codeInput)
                    .addAuthNote(AUTH_NOTE_OTP, codeInSession)
                    .build();

            doReturn(true).when(secureCode).isValid(
                    eq(codeInput),
                    eq(codeInSession),
                    any(),
                    eq(codeTimeout),
                    anyInt());

            authenticator.action(context);

            verify(context,times(0)).failure(any());
            verify(context,times(0)).failure(any(),any());
            verify(context,times(0)).failure(any(),any(),any(),any());
            verify(context,times(0)).failureChallenge(any(),any());
            verify(context,times(0)).challenge(any());
            verify(context,times(1)).success();
            verify(secureCode,times(1)).isValid(
                    eq(codeInput),
                    eq(codeInSession),
                    any(),
                    eq(codeTimeout),
                    anyInt());
        }
    }




}
