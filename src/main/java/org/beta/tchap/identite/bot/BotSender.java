package org.beta.tchap.identite.bot;

import org.beta.tchap.identite.matrix.rest.homeserver.HomeServerService;
import org.beta.tchap.identite.matrix.rest.login.LoginService;
import org.beta.tchap.identite.matrix.rest.room.RoomService;
import org.beta.tchap.identite.matrix.rest.user.UserInfoResource;
import org.beta.tchap.identite.matrix.rest.user.UserService;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;

import static org.beta.tchap.identite.matrix.rest.homeserver.HomeServerService.buildHomeServerUrl;

public class BotSender {

    private final RoomService roomService;

    public BotSender(RoomService roomService) {
        this.roomService = roomService;
    }

    public String openDM(String destMatrixId) {
        return roomService.createDM(destMatrixId);
    }

    public void sendMessage(String roomId, String otp) {
        roomService.sendMessage(roomId, "Bonjour, voici ton OTP !");
        roomService.sendMessage(roomId, otp);
    }


//    public void sendOtp(String otp, String email) {
//        String roomId = roomService.createDM(destMatrixId.getUserId());
//        roomService.sendMessage(roomId, "Bonjour, voici ton OTP !");
//        roomService.sendMessage(roomId, otp);
//    }
}
