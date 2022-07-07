/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.keycloak.matrix.rest.room;

public class SendMessageBody {
    private String body;
    private String msgtype = "m.direct";

    public SendMessageBody(String message) {
        this.body = message;
    }

    public String getBody() {
        return body;
    }

    public String getMsgtype() {
        return msgtype;
    }
}
