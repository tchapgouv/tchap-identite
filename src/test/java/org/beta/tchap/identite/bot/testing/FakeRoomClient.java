package org.beta.tchap.identite.bot.testing;

import org.beta.tchap.identite.matrix.rest.room.CreateDMBody;
import org.beta.tchap.identite.matrix.rest.room.RoomClient;
import org.beta.tchap.identite.matrix.rest.room.SendMessageBody;

import java.util.ArrayList;
import java.util.Map;

class FakeRoomClient implements RoomClient {
    public Map<String, ArrayList<String>> rooms;

    @Override
    public Map<String, ArrayList<String>> listDMRooms(String userId) {
        return rooms;
    }

    @Override
    public void updateDMRoomList(String userId, Map<String, ArrayList<String>> dMRoomsList) {

    }

    @Override
    public Map<String, String> createDM(CreateDMBody createDMBody) {
        return null;
    }

    @Override
    public void sendMessage(String roomId, SendMessageBody messageBody) {}
}
