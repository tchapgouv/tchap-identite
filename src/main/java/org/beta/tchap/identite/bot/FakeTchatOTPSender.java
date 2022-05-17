package org.beta.tchap.identite.bot;


import org.beta.tchap.identite.bot.exceptions.TchatOTPException;

public class FakeTchatOTPSender implements TchatOTPSender {
    private String otp;
    private String roomId;
    private TchatOTPException exceptionToThrow = null;

    @Override
    public void sendOTP(String otp, String matrixId) throws TchatOTPException {
        if (this.exceptionToThrow != null) {
            throw this.exceptionToThrow;
        }

        this.otp = otp;
        this.roomId = matrixId;
        System.out.println("Sent otp : ");
        System.out.println(otp);
        System.out.println("To dest : ");
        System.out.println(matrixId);
    }

    public String getOtp() {
        return otp;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setExceptionToThrow(TchatOTPException exceptionToThrow) {
        this.exceptionToThrow = exceptionToThrow;
    }
}
