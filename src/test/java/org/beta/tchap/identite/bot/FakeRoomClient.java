package org.beta.tchap.identite.bot;

import org.beta.tchap.identite.matrix.rest.room.CreateDMBody;
import org.beta.tchap.identite.matrix.rest.room.RoomClient;
import org.beta.tchap.identite.matrix.rest.room.SendMessageBody;

import java.util.*;

class FakeRoomClient implements RoomClient {
    public Map<String, List<String>> rooms;
    public String lastMessage = null;

    @Override
    public Map<String, List<String>> listDMRooms(String userId) {
        return rooms;
    }

    @Override
    public void updateDMRoomList(String userId, Map<String, List<String>> dMRoomsList) {
        this.rooms = dMRoomsList;
    }

    @Override
    public Map<String, String> createDM(CreateDMBody createDMBody) {
        String invitedId = createDMBody.getInvite().get(0);
        String roomId = "123";
        this.rooms.put(invitedId, List.of(roomId));

        Map<String, String> returnValue = new HashMap<>();
        returnValue.put("room_id", roomId);
        return returnValue;
    }

    @Override
    public void sendMessage(String roomId, String transactionId, SendMessageBody messageBody) {
        lastMessage = messageBody.getBody();
    }
}
