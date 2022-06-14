package org.beta.tchap.identite.matrix.rest.room;

import java.util.ArrayList;
import java.util.Map;

public class RoomService {
    private final RoomClient roomClient;

    public RoomService(RoomClient roomClient) {
        this.roomClient = roomClient;
    }

    public DirectRoomsResource listDMRooms() {
        Map<String, ArrayList<String>> rawResponse = roomClient.listDMRooms("@tchap-identite-tchap.beta.gouv.fr:i.tchap.gouv.fr");
        return DirectRoomsResource.toDirectRoomsResource(rawResponse);
    }

    public void updateDMRoomList(String userId, Map<String, ArrayList<String>> dMRoomsList ) {
        roomClient.updateDMRoomList(userId, dMRoomsList);
    }

    public String createDM(String destMatrixId) {
        DirectRoomsResource allRooms = this.listDMRooms();
        if (hasARoomWithUser(destMatrixId, allRooms)) {
            return allRooms.getDirectRoomsForMId(destMatrixId).get(0);
        }

        CreateDMBody body = new CreateDMBody();
        body.addInvite(destMatrixId);
        Map<String, String> response = roomClient.createDM(body);

        allRooms.addDirectRoomForMatrixId(destMatrixId, response.get("room_id"));
        String botAccount = "@tchap-identite-tchap.beta.gouv.fr:i.tchap.gouv.fr";
        roomClient.updateDMRoomList(botAccount, allRooms.getDirectRooms());

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
