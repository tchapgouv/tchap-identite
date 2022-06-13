package org.beta.tchap.identite.email;

public class EmailSenderFactory {
    private static EmailSender instance;

    public static EmailSender getInstance() {
        if (instance == null) {
            instance = new EmailSender();
        }
        return instance;
    }
}
