package org.beta.tchap.identite.matrix.rest.login;

import com.google.gson.Gson;
import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

import static org.beta.tchap.identite.matrix.rest.MatrixService.MATRIX_BASE_URL;

public class LoginClientFactory {

    public static LoginClient build(Gson gson) {
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new GsonEncoder(gson))
                .decoder(new GsonDecoder(gson))
                .logger(new Slf4jLogger(LoginClient.class))
                .logLevel(Logger.Level.FULL)
                .target(LoginClient.class, MATRIX_BASE_URL + "/_matrix/client/r0/login");
    }
}
