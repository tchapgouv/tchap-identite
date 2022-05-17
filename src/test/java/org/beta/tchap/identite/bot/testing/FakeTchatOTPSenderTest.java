package org.beta.tchap.identite.bot.testing;

import org.beta.tchap.identite.bot.FakeTchatOTPSender;
import org.beta.tchap.identite.bot.exceptions.TchatOTPException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FakeTchatOTPSenderTest {
    FakeTchatOTPSender sender = new FakeTchatOTPSender();

    @Test
    void should_not_fail_if_message_sent() {
        assertDoesNotThrow(() -> sender.sendOTP("1234", "testmatrixid"));
        assertEquals(sender.getOtp(), "1234");
        assertEquals(sender.getRoomId(), "testmatrixid");
    }

    @Test
    void should_throw_fail_if_message_not_sent() {
        sender.setExceptionToThrow(new TchatOTPException("Error sending message"));
        TchatOTPException exception = assertThrows(TchatOTPException.class,
                () -> sender.sendOTP("1234", "testmatrixid"));

        assertTrue(exception.getMessage().contains("Tchat OTP error:"));
        assertTrue(exception.getMessage().contains("Error sending message"));
    }

    @Test
    void should_throw_fail_if_user_not_found() {
        sender.setExceptionToThrow(new TchatOTPException("badmatrixid"));

        TchatOTPException exception = assertThrows(TchatOTPException.class,
                () -> sender.sendOTP("1234", "badmatrixid"));

        assertTrue(exception.getMessage().contains("badmatrixid"));
    }

}
