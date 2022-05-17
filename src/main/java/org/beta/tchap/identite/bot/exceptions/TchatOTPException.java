package org.beta.tchap.identite.bot.exceptions;

public class TchatOTPException extends Exception {
    public TchatOTPException(String errorMessage) {
        super("Tchat OTP error: " + errorMessage);
    }
}

