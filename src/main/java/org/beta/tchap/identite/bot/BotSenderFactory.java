package org.beta.tchap.identite.bot;

import org.beta.tchap.identite.matrix.rest.room.RoomClient;
import org.beta.tchap.identite.matrix.rest.room.RoomClientFactory;
import org.beta.tchap.identite.matrix.rest.room.RoomService;

public class BotSenderFactory {
    private static BotSender instance;

    public static BotSender getInstance(String accountHomeServerUrl, String accessToken) {
        if (instance == null) {
            RoomClient roomClient = RoomClientFactory.build(accountHomeServerUrl, accessToken);
            RoomService roomService = new RoomService(roomClient);
            instance = new BotSender(roomService);
        }
        return instance;
    }
}
