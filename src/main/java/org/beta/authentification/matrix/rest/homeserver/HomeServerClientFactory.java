/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.matrix.rest.homeserver;

import org.beta.authentification.matrix.rest.client.OkHttpClientFactory;
import org.beta.authentification.matrix.rest.gson.GsonFactory;

import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

public class HomeServerClientFactory {

    public static HomeServerClient build(String homeServerBaseUrl) {
        return Feign.builder()
                .client(new OkHttpClient(OkHttpClientFactory.getClient()))
                .encoder(new GsonEncoder(GsonFactory.getInstance()))
                .decoder(new GsonDecoder(GsonFactory.getInstance()))
                .logger(new Slf4jLogger(HomeServerClient.class))
                .logLevel(Logger.Level.FULL)
                .target(HomeServerClient.class, homeServerBaseUrl + "/_matrix/identity");
    }
}
