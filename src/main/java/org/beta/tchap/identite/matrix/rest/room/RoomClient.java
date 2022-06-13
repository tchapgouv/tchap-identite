package org.beta.tchap.identite.matrix.rest.room;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.beta.tchap.identite.matrix.rest.user.UserInfoBody;

import java.util.ArrayList;
import java.util.Map;

public interface RoomClient {
    @RequestLine("POST /createRoom")
    @Headers("Content-Type: application/json")
    Map<String, String> createDM(CreateDMBody createDMBody);

    @RequestLine("PUT /rooms/{roomId}/send/m.room.message/{nonce}")
    @Headers("Content-Type: application/json")
    void sendMessage(@Param("roomId") String roomId, SendMessageBody messageBody);
}
