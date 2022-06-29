package org.beta.tchap.identite.bot;

import org.beta.tchap.identite.matrix.rest.MatrixServiceFactory;

public class BotSenderFactory {
    private static BotSender instance;

    public static BotSender getInstance() {
        if (instance == null) {
            instance = new BotSender(MatrixServiceFactory.getInstance());
        }
        return instance;
    }
}
