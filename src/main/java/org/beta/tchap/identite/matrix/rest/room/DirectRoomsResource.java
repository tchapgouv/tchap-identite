package org.beta.tchap.identite.matrix.rest.room;

import java.util.ArrayList;
import java.util.Map;

public class DirectRoomsResource {
    private Map<String, ArrayList<String>> directRooms;

    public DirectRoomsResource() {
    }

    public Map<String, ArrayList<String>> getDirectRooms() {
        return this.directRooms;
    }

    public void setDirectRooms(Map<String, ArrayList<String>> directRooms) {
        this.directRooms = directRooms;
    }

    public ArrayList<String> getDirectRoomsForMId(String matrixId) {
        return this.directRooms.get(matrixId);
    }

    public void addDirectRoomForMatrixId(String matrixId, String roomId) {
        ArrayList<String> newRoomsId = this.getDirectRoomsForMId(matrixId);
        if (newRoomsId == null) {
            newRoomsId = new ArrayList<>();
        }
        newRoomsId.add(roomId);
        directRooms.put(matrixId, newRoomsId);
    }

    public static DirectRoomsResource toDirectRoomsResource(Map<String, ArrayList<String>> rawResponse) {
        DirectRoomsResource rooms = new DirectRoomsResource();
        rooms.setDirectRooms(rawResponse);
        return rooms;
    }
}
