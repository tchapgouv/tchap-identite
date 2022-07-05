/*
 * Copyright (c) 2022. DINUM
 * This·file·is·licensed·under·the·MIT·License,·see·LICENSE.md
 */

package org.beta.tchap.identite.matrix.rest.login;

import feign.Headers;
import feign.RequestLine;

public interface LoginClient {

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    LoginResource login(LoginBody loginBody);
}
