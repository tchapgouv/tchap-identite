package org.beta.tchap.identite.matrix.rest.room;

import org.beta.tchap.identite.matrix.exception.MatrixRuntimeException;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoomService {
    private final RoomClient roomClient;
    private final String botMatrixId;

    public RoomService(RoomClient roomClient) {
        this.roomClient = roomClient;
        this.botMatrixId = Environment.getenv(Constants.TCHAP_MATRIX_ID);
    }

    /**
     * Access the direct rooms listing in the bot account
     *
     * @return not nullable object
     * @throws MatrixRuntimeException direct rooms listing can not be retrieved
     */
    public DirectRoomsResource listBotDMRooms() {
        try {
            Map<String, List<String>> rawResponse = roomClient.listDMRooms(this.botMatrixId);
            return DirectRoomsResource.toDirectRoomsResource(rawResponse);
        } catch (RuntimeException e) {
            throw new MatrixRuntimeException();
        }
    }

    /**
     * Update the dm rooms listing of the bot account
     *
     * @param dMRoomsList not nullable
     * @throws MatrixRuntimeException direct rooms listing can not be updated
     */
    public void updateBotDMRoomList(Map<String, List<String>> dMRoomsList) {
        try {
            roomClient.updateDMRoomList(this.botMatrixId, dMRoomsList);
        } catch (RuntimeException e) {
            throw new MatrixRuntimeException();
        }
    }

    /**
     * Create a new direct message room with a user if not exists
     *
     * @param destMatrixId not nullable string
     * @return not nullable id of the room
     * @throws MatrixRuntimeException room can not be created
     */
    public String createDM(String destMatrixId) {
        try {
            DirectRoomsResource allRooms = this.listBotDMRooms();
            if (hasARoomWithUser(destMatrixId, allRooms) && isInvitedUserInRoom(destMatrixId, allRooms.getDirectRoomsForMId(destMatrixId).get(0))) {
                return allRooms.getDirectRoomsForMId(destMatrixId).get(0);
            }

            CreateDMBody body = new CreateDMBody();
            body.addInvite(destMatrixId);
            Map<String, String> response = roomClient.createDM(body);

            allRooms.addDirectRoomForMatrixId(destMatrixId, response.get("room_id"));
            roomClient.updateDMRoomList(this.botMatrixId, allRooms.getDirectRooms());

            return response.get("room_id");
        } catch (RuntimeException e) {
            throw new MatrixRuntimeException();
        }
    }

    /**
     * Send a message into an existing room
     *
     * @param roomId  non nullable string
     * @param message non nullable string
     * @throws MatrixRuntimeException if message is not sent
     */
    public void sendMessage(String roomId, String message) {
        try {
            SendMessageBody messageBody = new SendMessageBody(message);
            String transactionId = new Timestamp(System.currentTimeMillis()).toString();
            roomClient.sendMessage(roomId, transactionId, messageBody);
        } catch (RuntimeException e) {
            throw new MatrixRuntimeException();
        }
    }

    /**
     * Leave an existing room
     *
     * @param roomId non nullable string
     * @throws MatrixRuntimeException if can not leave room
     */
    public void leaveRoom(String roomId) {
        try {
            roomClient.leaveRoom(roomId);
        } catch (RuntimeException e) {
            throw new MatrixRuntimeException();
        }
    }

    public UsersListRessource getJoinedMembers(String roomId) {
        Map<String, Object> rawResponse = roomClient.getJoinedMembers(roomId);
        return UsersListRessource.toUsersListRessource((Map<String, Object>) rawResponse.get("joined"));
    }

    public boolean isInvitedUserInRoom(String userMId, String roomId) {
        return getJoinedMembers(roomId).getUsers().contains(userMId);
    }

    private boolean hasARoomWithUser(String destMatrixId, DirectRoomsResource rooms) {
        try {
            return rooms.getDirectRoomsForMId(destMatrixId) != null && rooms.getDirectRoomsForMId(destMatrixId).size() > 0;
        } catch (RuntimeException e) {
            throw new MatrixRuntimeException();
        }
    }


}