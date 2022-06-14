package org.beta.tchap.identite.bot.testing;

import org.beta.tchap.identite.matrix.rest.room.DirectRoomsResource;
import org.beta.tchap.identite.matrix.rest.room.RoomService;

class FakeRoomService extends RoomService {
    DirectRoomsResource rooms;

    public FakeRoomService() {
        super(null);
    }

    public String createDM() {
        return "";
    }
}
