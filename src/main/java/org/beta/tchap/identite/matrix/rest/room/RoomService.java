package org.beta.tchap.identite.matrix.rest.room;

import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;

import java.util.List;
import java.util.Map;

public class RoomService {
    private final RoomClient roomClient;
    private final String botMatrixId;

    public RoomService(RoomClient roomClient) {
        this.roomClient = roomClient;
        this.botMatrixId = Environment.getenv(Constants.TCHAP_MATRIX_ID);
    }

    public DirectRoomsResource listBotDMRooms() {
        Map<String, List<String>> rawResponse = roomClient.listDMRooms(this.botMatrixId);
        return DirectRoomsResource.toDirectRoomsResource(rawResponse);
    }

    public void updateBotDMRoomList(Map<String, List<String>> dMRoomsList) {
        roomClient.updateDMRoomList(this.botMatrixId, dMRoomsList);
    }

    public String createDM(String destMatrixId) {
        DirectRoomsResource allRooms = this.listBotDMRooms();
        if (hasARoomWithUser(destMatrixId, allRooms)) {
            return allRooms.getDirectRoomsForMId(destMatrixId).get(0);
        }

        CreateDMBody body = new CreateDMBody();
        body.addInvite(destMatrixId);
        Map<String, String> response = roomClient.createDM(body);

        allRooms.addDirectRoomForMatrixId(destMatrixId, response.get("room_id"));
        roomClient.updateDMRoomList(this.botMatrixId, allRooms.getDirectRooms());

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
