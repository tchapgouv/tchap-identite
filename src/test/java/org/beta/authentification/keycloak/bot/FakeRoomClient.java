/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.keycloak.bot;

import java.util.*;

import org.beta.authentification.matrix.rest.room.CreateDMBody;
import org.beta.authentification.matrix.rest.room.InviteBody;
import org.beta.authentification.matrix.rest.room.RoomClient;
import org.beta.authentification.matrix.rest.room.SendMessageBody;

class FakeRoomClient implements RoomClient {
    public Map<String, List<String>> rooms;
    public String lastMessage = null;
    public List<String> joinedMembers = new ArrayList<>();

    @Override
    public Map<String, List<String>> listDMRooms(String userId) {
        return rooms;
    }

    @Override
    public void updateDMRoomList(String userId, Map<String, List<String>> dMRoomsList) {
        this.rooms = dMRoomsList;
    }

    @Override
    public Map<String, Object> getJoinedMembers(String roomId) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> joined = new HashMap<>();
        for (String member : joinedMembers) {
            joined.put(member, null);
        }

        map.put("joined", joined);

        return map;
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
    public void leaveRoom(String roomId) {
        // TODO
    }

    @Override
    public void sendMessage(String roomId, String transactionId, SendMessageBody messageBody) {
        lastMessage = messageBody.getBody();
    }

    @Override
    public void join(String roomId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void invite(String roomId, InviteBody inviteBody) {
        // TODO Auto-generated method stub

    }
}
