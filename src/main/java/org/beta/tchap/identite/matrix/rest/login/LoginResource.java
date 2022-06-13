package org.beta.tchap.identite.matrix.rest.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResource {

    @JsonProperty("user_id")
    private String userId;

    private String accessToken;
    private String homeServer;
    private String deviceId;

    public LoginResource() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getHomeServer() {
        return homeServer;
    }

    public void setHomeServer(String homeServer) {
        this.homeServer = homeServer;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
