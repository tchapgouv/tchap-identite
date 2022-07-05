/*
 * Copyright (c) 2022. DINUM
 * This·file·is·licensed·under·the·MIT·License,·see·LICENSE.md
 */

package org.beta.tchap.identite.matrix.rest.user;

import feign.Headers;
import feign.RequestLine;
import java.util.Map;

public interface UserClient {

    @RequestLine("POST /users/info")
    @Headers("Content-Type: application/json")
    Map<String, Object> findByUsers(UserInfoBody userInfoBody);
}
