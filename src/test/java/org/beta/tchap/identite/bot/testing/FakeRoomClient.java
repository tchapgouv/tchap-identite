package org.beta.tchap.identite.bot.testing;

import org.beta.tchap.identite.matrix.rest.room.CreateDMBody;
import org.beta.tchap.identite.matrix.rest.room.RoomClient;
import org.beta.tchap.identite.matrix.rest.room.SendMessageBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class FakeRoomClient implements RoomClient {
    public Map<String, ArrayList<String>> rooms;
    public String lastMessage = null;

    @Override
    public Map<String, ArrayList<String>> listDMRooms(String userId) {
        return rooms;
    }

    @Override
    public void updateDMRoomList(String userId, Map<String, ArrayList<String>> dMRoomsList) {
        this.rooms = dMRoomsList;
    }

    @Override
    public Map<String, String> createDM(CreateDMBody createDMBody) {
        String invitedId = createDMBody.getInvite().get(0);
        String roomId = "123";
        this.rooms.put(invitedId, new ArrayList<>(Collections.singleton(roomId)));

        HashMap<String, String> returnValue = new HashMap<>();
        returnValue.put("room_id", roomId);
        return returnValue;
    }

    @Override
    public void sendMessage(String roomId, SendMessageBody messageBody) {
        lastMessage = messageBody.getBody();
    }
}