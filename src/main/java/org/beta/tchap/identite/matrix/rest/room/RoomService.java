package org.beta.tchap.identite.matrix.rest.room;

import org.beta.tchap.identite.matrix.rest.user.UserService;
import org.beta.tchap.identite.matrix.rest.user.DirectRoomsResource;

import java.util.Map;

public class RoomService {
    private final RoomClient roomClient;
    private final UserService userService;

    public RoomService(RoomClient roomClient, UserService userService) {
        this.roomClient = roomClient;
        this.userService = userService;
    }

    public String createDM(String destMatrixId) {
        DirectRoomsResource allRooms = userService.listDMRooms();
        if (hasARoomWithUser(destMatrixId, allRooms)) {
            return allRooms.getDirectRoomsForMId(destMatrixId).get(0);
        }

        CreateDMBody body = new CreateDMBody();
        body.addInvite(destMatrixId);
        Map<String, String> response = roomClient.createDM(body);

        allRooms.addDirectRoomForMatrixId(destMatrixId, response.get("room_id"));
        String botAccount = "@tchap-identite-tchap.beta.gouv.fr:i.tchap.gouv.fr";
        userService.updateDMRoomList(botAccount, allRooms.getDirectRooms());

        return response.get("room_id");
    }

    private boolean hasARoomWithUser(String destMatrixId, DirectRoomsResource rooms) {
        return rooms.getDirectRoomsForMId(destMatrixId) != null && rooms.getDirectRoomsForMId(destMatrixId).size() > 0;
    }

    public void sendMessage(String roomId, String message) {
        SendMessageBody messageBody = new SendMessageBody(message);
        roomClient.sendMessage(roomId, messageBody);
    }
}
