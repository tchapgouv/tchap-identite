package org.beta.tchap.identite.matrix.rest.login;

import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.beta.tchap.identite.matrix.rest.client.OkHttpClientFactory;
import org.beta.tchap.identite.matrix.rest.gson.GsonFactory;

import static org.beta.tchap.identite.matrix.rest.MatrixService.MATRIX_BASE_URL;

public class LoginClientFactory {

    public static LoginClient build() {
        return Feign.builder()
                .client(new OkHttpClient(OkHttpClientFactory.getUnsafeOkHttpClient()))
                .encoder(new GsonEncoder(GsonFactory.getInstance()))
                .decoder(new GsonDecoder(GsonFactory.getInstance()))
                .logger(new Slf4jLogger(LoginClient.class))
                .logLevel(Logger.Level.FULL)
                .target(LoginClient.class, MATRIX_BASE_URL + "/_matrix/client/r0/login");
    }
}
