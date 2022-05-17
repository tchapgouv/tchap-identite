package org.beta.tchap.identite.bot;

public interface TchatOTPSender {
    void sendOTP(String otp, String matrixId) throws Exception;
}
