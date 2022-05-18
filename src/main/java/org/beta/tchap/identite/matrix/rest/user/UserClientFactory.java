package org.beta.tchap.identite.matrix.rest.user;

import com.google.gson.Gson;
import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.beta.tchap.identite.matrix.rest.login.LoginResource;

import static org.beta.tchap.identite.matrix.rest.MatrixService.MATRIX_BASE_URL;

public class UserClientFactory {

    public static UserClient build(Gson gson, LoginResource loginResource) {
        return Feign.builder()
                .client(new OkHttpClient())
                .requestInterceptor(requestTemplate ->
                        requestTemplate.header(
                                "Authorization",
                                "Bearer " + loginResource.getAccessToken()))
                .encoder(new GsonEncoder(gson))
                .decoder(new GsonDecoder(gson))
                .logger(new Slf4jLogger(UserClient.class))
                .logLevel(Logger.Level.FULL)
                .target(UserClient.class, MATRIX_BASE_URL + "/_matrix/client/unstable/users");
    }
}
