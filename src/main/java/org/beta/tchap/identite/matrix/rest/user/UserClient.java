package org.beta.tchap.identite.matrix.rest.user;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.ArrayList;
import java.util.Map;

public interface UserClient {

    @RequestLine("POST /users/info")
    @Headers("Content-Type: application/json")
    Map<String,Object> findByUsers(UserInfoBody userInfoBody);

    @RequestLine("GET /user/{userId}/account_data/m.direct")
    @Headers("Content-Type: application/json")
    Map<String, ArrayList<String>> listDMRooms(@Param("userId") String userId);

    @RequestLine("PUT /user/{userId}/account_data/m.direct")
    @Headers("Content-Type: application/json")
    void updateDMRoomList(@Param("userId") String userId, Map<String, ArrayList<String>> dMRoomsList);
}
