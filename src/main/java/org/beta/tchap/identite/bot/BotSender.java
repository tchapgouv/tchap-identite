package org.beta.tchap.identite.bot;

import org.beta.tchap.identite.matrix.rest.room.RoomService;

public class BotSender {

    private final RoomService roomService;

    public BotSender(RoomService roomService) {
        this.roomService = roomService;
    }

    public void sendOtp(String otp, String destMatrixId) {
        String roomId = roomService.createDM(destMatrixId);
        roomService.sendMessage(roomId, otp);
    }
}
