package org.beta.tchap.identite.matrix.rest.user;

import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.beta.tchap.identite.matrix.rest.client.OkHttpClientFactory;
import org.beta.tchap.identite.matrix.rest.gson.GsonFactory;

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
                .target(UserClient.class, homeServerBaseUrl + "/_matrix/client/unstable/users");
    }
}
