/*
 * Copyright (c) 2022. DINUM
 */

package org.beta.tchap.identite.matrix.rest.room;

import org.beta.tchap.identite.matrix.exception.MatrixRuntimeException;
import org.beta.tchap.identite.matrix.exception.RoomDoesNotExist;
import org.beta.tchap.identite.matrix.exception.UserDoesNotExist;

import feign.FeignException.BadRequest;
import feign.FeignException.Forbidden;
import feign.FeignException.NotFound;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

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
     * update the room account data
     * @param destMatrixId
     * @param roomId
     */
    protected void updateRoomAccounData(String destMatrixId, String roomId){
        if(roomId==null || destMatrixId == null){
            throw new IllegalArgumentException(String.format("Nor roomId nor destMatrixId must be null - roomId%s userId:%s", roomId, destMatrixId));
        }
        try {
            //update account data
            DirectRoomsResource allRooms = this.listBotDMRooms();
            allRooms.addDirectRoomForMatrixId(destMatrixId, roomId);
            roomClient.updateDMRoomList(this.botMatrixId, allRooms.getDirectRooms());
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
        }
    }

    /**
     * Create a new direct message room with a user if not exists, an invit is sent
     *
     * @param destMatrixId not nullable string
     * @return not nullable id of the room
     * @throws UserDoesNotExist user does not exists
     * @throws MatrixRuntimeException room can not be created
     */
    public String createDM(String destMatrixId) {
        if(destMatrixId == null){
            throw new IllegalArgumentException(String.format("destMatrixId must be not null - userId:%s",destMatrixId));
        }
        try {
            CreateDMBody body = new CreateDMBody();
            body.addInvite(destMatrixId);
            Map<String, String> response = roomClient.createDM(body);
            String roomId = response.get("room_id");
            this.updateRoomAccounData(destMatrixId, roomId);
            return roomId;
        }catch(BadRequest e){
            //todo : should be parsed to see if the problem comes from the user
            throw new MatrixRuntimeException(e);
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
        }
    }



    /**
     * Send a message into an existing room
     *
     * @param roomId  non nullable string
     * @param message non nullable string
     * @throws RoomDoesNotExist if room does not exist
     * @throws MatrixRuntimeException if message is not sent
     */
    public void sendMessage(String roomId, String message) {
        if(roomId == null|| message == null){
            throw new IllegalArgumentException(String.format("Nor roomId nor message must be null - roomId%s message:%s", roomId, message));
        }
        try {
            SendMessageBody messageBody = new SendMessageBody(message);
            String transactionId = new Timestamp(System.currentTimeMillis()).toString();
            roomClient.sendMessage(roomId, transactionId, messageBody);
        }catch(Forbidden e){
            throw new RoomDoesNotExist(e);
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
        }
    }

    /**
     * Invite a userId to an existing room
     * @param roomId non nullable string
     * @param userId non nullable string
     * @throws RoomDoesNotExist if room does not exist
     * @throws MatrixRuntimeException if can not invite user into room
     */
    public void invite(String roomId, String userId) {
        if(roomId==null || userId == null){
            throw new IllegalArgumentException(String.format("Nor roomId nor userId must be null - roomId%s userId:%s", roomId, userId));
        }
        try{
            roomClient.invite(roomId, new InviteBody(userId));
        }catch(Forbidden e){
            throw new RoomDoesNotExist(e);
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
        }
    }

    /**
     * Return the list of users that have joined a room (including the creator of the room)
     * @param roomId
     * @return
    * @throws MatrixRuntimeException if can get the list of joined members
     */
    public UsersListRessource getJoinedMembers(String roomId) {
        if(roomId == null){
            throw new IllegalArgumentException(String.format("roomId must be not null - roomId:%s",roomId));
        }
        try {
            Map<String, Object> rawResponse = roomClient.getJoinedMembers(roomId);
            return UsersListRessource.toUsersListRessource((Map<String, Object>) rawResponse.get("joined"));
        }catch(Forbidden e){
            throw new RoomDoesNotExist(e);
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
        }
    }

    /**
     * Leave an existing room (only for testing)
     *
     * @param roomId non nullable string
     * @throws MatrixRuntimeException if can not leave room
     */
    public void leaveRoom(String roomId) {
        if(roomId == null){
            throw new IllegalArgumentException(String.format("roomId must be not null - roomId:%s",roomId));
        }
        try {
            roomClient.leaveRoom(roomId);
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
        }
    }

    /**
     * Join an existing room (only for testing)
     * @param roomId non nullable string
     * @throws MatrixRuntimeException if can not join room
     */
    public void join(String roomId) {
        if(roomId == null){
            throw new IllegalArgumentException(String.format("roomId must be not null - roomId:%s",roomId));
        }
        try{
            roomClient.join(roomId);
        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
        }
    }

    /**
     * Util methods. -- should not be here
     * @param destMatrixId
     * @param rooms
     * @return
     */
    public static boolean hasARoomWithUser(String destMatrixId, DirectRoomsResource rooms) {
        List<String> directRoomsForMId = rooms.getDirectRoomsForMId(destMatrixId);
        return directRoomsForMId != null && directRoomsForMId.size() > 0;
    }


}
