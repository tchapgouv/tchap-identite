/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.keycloak.matrix.rest.room;

import java.util.*;

public class DirectRoomsResource {
    private Map<String, List<String>> directRooms = new HashMap<>();

    public DirectRoomsResource() {
        directRooms = new HashMap<>();
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
        List<String> newRoomsId =
                Optional.ofNullable(getDirectRoomsForMId(matrixId))
                        .map(ArrayList::new)
                        .orElse(new ArrayList<>());
        newRoomsId.add(roomId);
        directRooms.put(matrixId, newRoomsId);
    }

    public static DirectRoomsResource toDirectRoomsResource(Map<String, List<String>> rawResponse) {
        DirectRoomsResource rooms = new DirectRoomsResource();
        rooms.setDirectRooms(rawResponse);
        return rooms;
    }
}
