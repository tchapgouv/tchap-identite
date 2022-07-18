/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.matrix.rest.room;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CreateRoomBody {
    private final List<String> invite = new ArrayList<>();

    private final String name;
    private final String preset = "private_chat";
    private final String visibility = "private";
    private final String access_rules = "restricted";
    private final List<Object> initial_state =
            new Gson()
                    .fromJson(
                            "[\n" +
                                    "    {\n" +
                                    "        \"content\": {\n" +
                                    "            \"guest_access\": \"forbidden\"\n" +
                                    "        },\n" +
                                    "        \"type\": \"m.room.guest_access\",\n" +
                                    "        \"state_key\": \"\"\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "        \"content\": {\n" +
                                    "            \"history_visibility\": \"invited\"\n" +
                                    "        },\n" +
                                    "        \"type\": \"m.room.history_visibility\",\n" +
                                    "        \"state_key\": \"\"\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "        \"content\": {\n" +
                                    "            \"rule\": \"restricted\"\n" +
                                    "        },\n" +
                                    "        \"type\": \"im.vector.room.access_rules\",\n" +
                                    "        \"state_key\": \"\"\n" +
                                    "    }\n" +
                                    "]",
                            ArrayList.class);

    public CreateRoomBody(String roomName) {
        this.name = roomName;
    }

    public void addInvite(String matrixId) {
        this.invite.add(matrixId);
    }

    public List<String> getInvite() {
        return invite;
    }

    public String getPreset() {
        return preset;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getAccess_rules() {
        return access_rules;
    }

    public List<Object> getInitial_state() {
        return initial_state;
    }

    public String getName() {
        return name;
    }
}
