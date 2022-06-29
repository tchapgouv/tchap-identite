package org.beta.tchap.identite.matrix.rest.room;

import org.beta.tchap.identite.matrix.exception.MatrixRuntimeException;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;

import feign.FeignException.NotFound;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoomService {
    private final RoomClient roomClient;
    private final String botMatrixId;

    public RoomService(RoomClient roomClient, String userId) {
        this.roomClient = roomClient;
        //this.botMatrixId = Environment.getenv(Constants.TCHAP_MATRIX_ID);
        this.botMatrixId = userId;
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
        }catch(NotFound e){
            return new DirectRoomsResource();//return empty map
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
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
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
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
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
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
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
        }
    }

    /**
     * Invite a userId to an existing room
     * @param roomId non nullable string
     * @param userId non nullable string
     * @throws MatrixRuntimeException if can not invite user into room
     */
    public void invite(String roomId, String userId) {
        if(roomId==null || userId == null){
            throw new IllegalArgumentException(String.format("Nor roomId nor userId must be null - roomId%s userId:%s", roomId, userId));
        }
        try{
            roomClient.invite(roomId, new InviteBody(userId));
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
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
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
        }
    }

    /**
     * Join an existing room
     * @param roomId non nullable string
     * @throws MatrixRuntimeException if can not join room
     */
    public void join(String roomId) {
        if(roomId==null){
            throw new IllegalArgumentException(String.format("RoomId must not be null - roomId%s", roomId));
        }
        try{
            roomClient.join(roomId);
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
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
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
        }
    }


}
