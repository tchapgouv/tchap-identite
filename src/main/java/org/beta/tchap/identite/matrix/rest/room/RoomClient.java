package org.beta.tchap.identite.matrix.rest.room;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoomClient {
    @RequestLine("GET /user/{userId}/account_data/m.direct")
    @Headers("Content-Type: application/json")
    Map<String, List<String>> listDMRooms(@Param("userId") String userId);

    @RequestLine("PUT /user/{userId}/account_data/m.direct")
    @Headers("Content-Type: application/json")
    void updateDMRoomList(@Param("userId") String userId, Map<String, List<String>> dMRoomsList);

//    @RequestLine("GET /rooms/{roomId}/state/{eventType}/{stateKey}")
//    @Headers("Content-Type: application/json")
//    Object getRoomEvents(@Param("roomId") String roomId, @Param("eventType") String eventType);

    @RequestLine("GET /rooms/{roomId}/joined_members")
    @Headers("Content-Type: application/json")
    Map<String, Object> getJoinedMembers(@Param("roomId") String roomId);

    @RequestLine("POST /createRoom")
    @Headers("Content-Type: application/json")
    Map<String, String> createDM(CreateDMBody createDMBody);

    @RequestLine("POST /rooms/{roomId}/leave")
    @Headers("Content-Type: application/json")
    void leaveRoom(@Param("roomId") String roomId);

    @RequestLine("PUT /rooms/{roomId}/send/m.room.message/{transactionId}")
    @Headers("Content-Type: application/json")
    void sendMessage(@Param("roomId") String roomId, @Param("transactionId") String transactionId, SendMessageBody messageBody);
}
