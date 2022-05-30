package org.beta.tchap.identite.matrix.rest.user;

import java.util.Map;

public class DirectRoomsResource {
    private Map<String, String[]>directRooms;

    public DirectRoomsResource() {
    }

    public Map<String, String[]> getDirectRooms() {
        return directRooms;
    }

    public void setDirectRooms(Map<String, String[]> directRooms) {
        this.directRooms = directRooms;
    }

//    public static DirectRoomsResource toDirectRoomsResource(Map<String, Object> rawResponse) {
//        System.out.println(rawResponse);
//
//        return null;
//    }
}
