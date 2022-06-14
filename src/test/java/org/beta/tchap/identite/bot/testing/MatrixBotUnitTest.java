package org.beta.tchap.identite.bot.testing;

import org.apache.log4j.BasicConfigurator;
import org.beta.tchap.identite.matrix.rest.room.DirectRoomsResource;
import org.beta.tchap.identite.matrix.rest.room.RoomClient;
import org.beta.tchap.identite.matrix.rest.room.RoomService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;


// FIXME need cleanup of rooms before/after each test
class MatrixBotUnitTest {
    private static FakeRoomClient roomClient;
    private static RoomService roomService;

    @BeforeAll
    static void setup() {
        // Needed for logging
        BasicConfigurator.configure();
        roomClient = new FakeRoomClient();
        roomClient.rooms = new HashMap<>();
        roomService = new RoomService(roomClient);
    }

    @Nested
    class ListRoomsTest {
        @Test
        void shouldListDMRooms() {
            roomClient.rooms.put("testId", new ArrayList<>(2));
            DirectRoomsResource rooms = roomService.listDMRooms();
            Assertions.assertTrue(rooms.getDirectRooms().size() > 0);
        }

    }

    @Nested
    class NoEventsTest {
        @Test
        void shouldHaveNoDMEventsIfNoDM() {
            String destId = "@calev.eliacheff-beta.gouv.fr:i.tchap.gouv.fr";

            DirectRoomsResource dmRooms = roomService.listDMRooms();
            Assertions.assertNull(dmRooms.getDirectRoomsForMId(destId));
        }
    }

    @Nested
    class CreateDMTest {
        @Test
        void shouldCreateADMAndAddDMEvent() {
            String destId = "@calev.eliacheff-beta.gouv.fr:i.tchap.gouv.fr";
            String roomId = roomService.createDM(destId);

            DirectRoomsResource dmRooms = roomService.listDMRooms();
            Assertions.assertNotNull(roomId);
            Assertions.assertNotNull(dmRooms.getDirectRoomsForMId(destId));
            Assertions.assertTrue(dmRooms.getDirectRoomsForMId(destId).size() > 0);
        }

        @Test
        void shouldNotCreateADMIfADMWithUserExists() {
            String destId = "@calev.eliacheff-beta.gouv.fr:i.tchap.gouv.fr";
            String roomId1 = roomService.createDM(destId);
            String roomId2 = roomService.createDM(destId);

            Assertions.assertEquals(roomId1, roomId2);
        }
    }

    @Nested
    class SendingMessagesTest {
        @Test
        void shouldSendAMessageToADMRoom() {
            String destId = "@calev.eliacheff-beta.gouv.fr:i.tchap.gouv.fr";
            String roomId = roomService.createDM(destId);
            Assertions.assertDoesNotThrow(() -> roomService.sendMessage(roomId, "Hello world"));
        }
    }
}
