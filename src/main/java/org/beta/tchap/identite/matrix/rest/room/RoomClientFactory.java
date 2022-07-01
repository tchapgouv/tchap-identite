/*
 * Copyright (c) 2022. DINUM
 */

package org.beta.tchap.identite.matrix.rest.room;

import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.beta.tchap.identite.matrix.rest.client.OkHttpClientFactory;
import org.beta.tchap.identite.matrix.rest.gson.GsonFactory;

public class RoomClientFactory {

    public static RoomClient build(String homeServerBaseUrl, String accessToken) {
        return Feign.builder()
                .client(new OkHttpClient(OkHttpClientFactory.getClient()))
                .requestInterceptor(requestTemplate ->
                        requestTemplate.header(
                                "Authorization",
                                "Bearer " + accessToken))
                .encoder(new GsonEncoder(GsonFactory.getInstance()))
                .decoder(new GsonDecoder(GsonFactory.getInstance()))
                .logger(new Slf4jLogger(RoomClient.class))
                .logLevel(Logger.Level.FULL)
                .target(RoomClient.class, homeServerBaseUrl + "/_matrix/client/unstable");
    }
}
