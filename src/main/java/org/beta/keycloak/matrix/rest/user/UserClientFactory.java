/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.keycloak.matrix.rest.user;

import org.beta.keycloak.matrix.rest.client.OkHttpClientFactory;
import org.beta.keycloak.matrix.rest.gson.GsonFactory;

import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

public class UserClientFactory {

    public static UserClient build(String homeServerBaseUrl, String accessToken) {
        return Feign.builder()
                .client(new OkHttpClient(OkHttpClientFactory.getClient()))
                .requestInterceptor(
                        requestTemplate ->
                                requestTemplate.header("Authorization", "Bearer " + accessToken))
                .encoder(new GsonEncoder(GsonFactory.getInstance()))
                .decoder(new GsonDecoder(GsonFactory.getInstance()))
                .logger(new Slf4jLogger(UserClient.class))
                .logLevel(Logger.Level.FULL)
                .target(UserClient.class, homeServerBaseUrl + "/_matrix/client/unstable");
    }
}
