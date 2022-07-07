/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.keycloak.matrix.rest.login;

import org.beta.keycloak.matrix.rest.client.OkHttpClientFactory;
import org.beta.keycloak.matrix.rest.gson.GsonFactory;

import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

public class LoginClientFactory {

    public static LoginClient build(String homeServerBaseUrl) {
        return Feign.builder()
                .client(new OkHttpClient(OkHttpClientFactory.getClient()))
                .encoder(new GsonEncoder(GsonFactory.getInstance()))
                .decoder(new GsonDecoder(GsonFactory.getInstance()))
                .logger(new Slf4jLogger(LoginClient.class))
                .logLevel(Logger.Level.FULL)
                .target(LoginClient.class, homeServerBaseUrl + "/_matrix/client/r0/login");
    }
}
