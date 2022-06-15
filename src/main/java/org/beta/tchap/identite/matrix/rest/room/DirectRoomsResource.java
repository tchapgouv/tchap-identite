package org.beta.tchap.identite.matrix.rest.room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DirectRoomsResource {
    private Map<String, List<String>> directRooms;

    public DirectRoomsResource() {
    }

    public Map<String, List<String>> getDirectRooms() {
        return directRooms;
    }

    public void setDirectRooms(Map<String, List<String>> directRooms) {
        this.directRooms = directRooms;
    }

    public List<String> getDirectRoomsForMId(String matrixId) {
        return directRooms.get(matrixId);
    }

    public void addDirectRoomForMatrixId(String matrixId, String roomId) {
        List<String> newRoomsId = new ArrayList<>(getDirectRoomsForMId(matrixId));
        newRoomsId.add(roomId);
        directRooms.put(matrixId, newRoomsId);
    }

    public static DirectRoomsResource toDirectRoomsResource(Map<String, List<String>> rawResponse) {
        DirectRoomsResource rooms = new DirectRoomsResource();
        rooms.setDirectRooms(rawResponse);
        return rooms;
    }
}
