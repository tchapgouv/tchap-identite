package org.beta.tchap.identite.bot.testing;

import org.apache.log4j.BasicConfigurator;
import org.beta.tchap.identite.matrix.rest.room.DirectRoomsResource;
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
    private final String testAccountMatrixId = "@calev.eliacheff-beta.gouv.fr:i.tchap.gouv.fr";


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
            DirectRoomsResource rooms = roomService.listBotDMRooms();
            Assertions.assertTrue(rooms.getDirectRooms().size() > 0);
        }

    }

    @Nested
    class NoEventsTest {
        @Test
        void shouldHaveNoDMEventsIfNoDM() {
            DirectRoomsResource dmRooms = roomService.listBotDMRooms();
            Assertions.assertNull(dmRooms.getDirectRoomsForMId(testAccountMatrixId));
        }
    }

    @Nested
    class CreateDMTest {
        @Test
        void shouldCreateADMAndAddDMEvent() {
            String roomId = roomService.createDM(testAccountMatrixId);

            DirectRoomsResource dmRooms = roomService.listBotDMRooms();
            Assertions.assertNotNull(roomId);
            Assertions.assertNotNull(dmRooms.getDirectRoomsForMId(testAccountMatrixId));
            Assertions.assertTrue(dmRooms.getDirectRoomsForMId(testAccountMatrixId).size() > 0);
        }

        @Test
        void shouldNotCreateADMIfADMWithUserExists() {
            String roomId1 = roomService.createDM(testAccountMatrixId);
            String roomId2 = roomService.createDM(testAccountMatrixId);

            Assertions.assertEquals(roomId1, roomId2);
        }
    }

    @Nested
    class SendingMessagesTest {
        @Test
        void shouldSendAMessageToADMRoom() {
            String roomId = roomService.createDM(testAccountMatrixId);
            Assertions.assertDoesNotThrow(() -> roomService.sendMessage(roomId, "Hello world"));
            Assertions.assertEquals(roomClient.lastMessage, "Hello world");
        }
    }
}
