/*
 * Copyright (c) 2022 DINUM
 * This file is Licensed under the MIT License, see LICENSE.md
 */

package org.beta.tchap.identite.matrix.rest.room;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CreateDMBody {
    private final List<String> invite = new ArrayList<>();

    private final String type = "m.login.password";
    private final Boolean isDirect = true;
    private final String preset = "trusted_private_chat";
    private final String visibility = "private";
    private final String access_rules = "direct";
    private final List<Object> initial_state = new Gson().fromJson("[\n" +
            "                {\"content\":{\"guest_access\":\"forbidden\"},\n" +
            "                    \"type\":\"m.room.guest_access\",\"state_key\":\"\"},\n" +
            "                {\"content\":{\"history_visibility\":\"invited\"},\n" +
            "                    \"type\":\"m.room.history_visibility\",\"state_key\":\"\"},\n" +
            "                {\"content\":{\"rule\":\"direct\"},\n" +
            "                    \"type\":\"im.vector.room.access_rules\",\"state_key\":\"\"}]", ArrayList.class);


    public CreateDMBody() {
    }

    public void addInvite(String matrixId) {
        this.invite.add(matrixId);
    }

    public List<String> getInvite() {
        return invite;
    }

    public String getType() {
        return type;
    }

    public Boolean getDirect() {
        return isDirect;
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
}
