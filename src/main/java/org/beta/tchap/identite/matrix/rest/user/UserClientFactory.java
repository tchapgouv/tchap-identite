package org.beta.tchap.identite.matrix.rest.user;

import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.beta.tchap.identite.matrix.rest.client.OkHttpClientFactory;
import org.beta.tchap.identite.matrix.rest.gson.GsonFactory;
import org.beta.tchap.identite.matrix.rest.login.LoginResource;

public class UserClientFactory {

    public static UserClient build(LoginResource loginResource, String homeServerBaseUrl) {
        return Feign.builder()
                .client(new OkHttpClient(OkHttpClientFactory.getUnsafeOkHttpClient()))
                .requestInterceptor(requestTemplate ->
                        requestTemplate.header(
                                "Authorization",
                                "Bearer " + loginResource.getAccessToken()))
                .encoder(new GsonEncoder(GsonFactory.getInstance()))
                .decoder(new GsonDecoder(GsonFactory.getInstance()))
                .logger(new Slf4jLogger(UserClient.class))
                .logLevel(Logger.Level.FULL)
                .target(UserClient.class, homeServerBaseUrl + "/_matrix/client/unstable/users");
    }
}
