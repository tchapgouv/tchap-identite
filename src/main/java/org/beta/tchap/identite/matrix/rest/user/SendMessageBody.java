package org.beta.tchap.identite.matrix.rest.user;

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
