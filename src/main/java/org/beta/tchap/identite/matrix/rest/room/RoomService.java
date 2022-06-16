package org.beta.tchap.identite.matrix.rest.room;

import org.beta.tchap.identite.matrix.exception.MatrixRuntimeException;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;

import java.sql.Timestamp;
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
        try{
            Map<String, List<String>> rawResponse = roomClient.listDMRooms(this.botMatrixId);
            return DirectRoomsResource.toDirectRoomsResource(rawResponse);
        }catch(RuntimeException e){
            throw new MatrixRuntimeException();
        }
    }

    public void updateBotDMRoomList(Map<String, List<String>> dMRoomsList) {
        try{
            roomClient.updateDMRoomList(this.botMatrixId, dMRoomsList);
        }catch(RuntimeException e){
            throw new MatrixRuntimeException();
        }
    }

    public String createDM(String destMatrixId) {
        try{
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
        }catch(RuntimeException e){
            throw new MatrixRuntimeException();
        }
    }

    private boolean hasARoomWithUser(String destMatrixId, DirectRoomsResource rooms) {
        try{
            return rooms.getDirectRoomsForMId(destMatrixId) != null && rooms.getDirectRoomsForMId(destMatrixId).size() > 0;
        }catch(RuntimeException e){
            throw new MatrixRuntimeException();
        }
    }

    public void sendMessage(String roomId, String message) {
        try{
            SendMessageBody messageBody = new SendMessageBody(message);
            String transactionId = new Timestamp(System.currentTimeMillis()).toString();
            roomClient.sendMessage(roomId, transactionId, messageBody);
        }catch(RuntimeException e){
            throw new MatrixRuntimeException();
        }
    }

    public void leaveRoom(String roomId) {
        try{
            roomClient.leaveRoom(roomId);
        }catch(RuntimeException e){
            throw new MatrixRuntimeException();
        }
    }
}
