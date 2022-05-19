package org.beta.tchap.identite.matrix.rest.homeserver;

public class HomeServerInfoQuery {
    private final String medium;
    private final String address;

    public HomeServerInfoQuery(String medium, String address) {
        this.medium = medium;
        this.address = address;
    }

    public String getMedium() {
        return medium;
    }

    public String getAddress() {
        return address;
    }
}
