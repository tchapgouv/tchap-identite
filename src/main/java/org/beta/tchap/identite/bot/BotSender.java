package org.beta.tchap.identite.bot;

import org.beta.tchap.identite.matrix.rest.user.*;

import java.util.Map;

public class BotSender {

    private final UserService userService;
    private final UserClient userClient;

    public BotSender(UserService userService, UserClient userClient) {
        this.userService = userService;
        this.userClient = userClient;
    }

    public void sendOtp(String otp, String destMatrixId) {
        String roomId = createDM(destMatrixId);
        sendMessage(roomId, otp);
    }

    private String createDM(String destMatrixId) {
        DirectRoomsResource allRooms = userService.listDMRooms();
        if (hasARoomWithUser(destMatrixId, allRooms)) {
            return allRooms.getDirectRoomsForMId(destMatrixId).get(0);
        }

        CreateDMBody body = new CreateDMBody();
        body.addInvite(destMatrixId);
        Map<String, String> response = userClient.createDM(body);

        allRooms.addDirectRoomForMatrixId(destMatrixId, response.get("room_id"));
        String botAccount = "@tchap-identite-tchap.beta.gouv.fr:i.tchap.gouv.fr";
        userClient.updateDMRoomList(botAccount, allRooms.getDirectRooms());

        return response.get("room_id");
    }

    private boolean hasARoomWithUser(String destMatrixId, DirectRoomsResource rooms) {
        return rooms.getDirectRoomsForMId(destMatrixId) != null && rooms.getDirectRoomsForMId(destMatrixId).size() > 0;
    }

    private void sendMessage(String roomId, String message) {
        SendMessageBody messageBody = new SendMessageBody(message);
        userClient.sendMessage(roomId, messageBody);
    }
}
