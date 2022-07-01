/*
 * Copyright (c) 2022. DINUM
 */

package org.beta.tchap.identite.matrix.rest.room;

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
