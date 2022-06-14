package org.beta.tchap.identite.matrix.rest.room;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.ArrayList;
import java.util.Map;

public interface RoomClient {
    @RequestLine("GET /user/{userId}/account_data/m.direct")
    @Headers("Content-Type: application/json")
    Map<String, ArrayList<String>> listDMRooms(@Param("userId") String userId);

    @RequestLine("PUT /user/{userId}/account_data/m.direct")
    @Headers("Content-Type: application/json")
    void updateDMRoomList(@Param("userId") String userId, Map<String, ArrayList<String>> dMRoomsList);

    @RequestLine("POST /createRoom")
    @Headers("Content-Type: application/json")
    Map<String, String> createDM(CreateDMBody createDMBody);

    @RequestLine("PUT /rooms/{roomId}/send/m.room.message/{nonce}")
    @Headers("Content-Type: application/json")
    void sendMessage(@Param("roomId") String roomId, SendMessageBody messageBody);
}
