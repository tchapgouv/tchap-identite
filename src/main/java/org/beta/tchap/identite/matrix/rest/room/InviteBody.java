/*
 * Copyright (c) 2022. DINUM
 */

package org.beta.tchap.identite.matrix.rest.room;

public class InviteBody {
    private String user_id;

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public InviteBody(String user_id) {
        this.user_id = user_id;
    }

}
