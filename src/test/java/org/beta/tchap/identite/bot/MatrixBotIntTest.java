package org.beta.tchap.identite.bot;

import org.apache.log4j.BasicConfigurator;
import org.beta.tchap.TestSuiteUtils;
import org.beta.tchap.identite.matrix.rest.MatrixService;
import org.beta.tchap.identite.matrix.rest.MatrixServiceFactory;
import org.beta.tchap.identite.matrix.rest.room.DirectRoomsResource;
import org.beta.tchap.identite.matrix.rest.room.RoomService;
import org.beta.tchap.identite.utils.Environment;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



// FIXME should test the events in the rooms to be sure it's ok
// FIXME: errors to catch: feign.FeignException$Forbidden
class MatrixBotIntTest {
    private static RoomService roomService;
    private static MatrixService matrixService;
    private static String testAccountMatrixId;

    private final List<String> createdTestRooms = new ArrayList<>();
    private static Boolean deleteRoomAfterTests;
    @BeforeAll
    public static void setup() {
        // Needed for logging
        BasicConfigurator.configure();
        TestSuiteUtils.loadEnvFromDotEnvFile();
        
        deleteRoomAfterTests = Environment.getenv(TestSuiteUtils.ENV_DELETE_ROOM_AFTER_TESTS) == null 
        || !Environment.getenv(TestSuiteUtils.ENV_DELETE_ROOM_AFTER_TESTS).toLowerCase().equals("false");
        testAccountMatrixId = Environment.getenv(TestSuiteUtils.ENV_TEST_BOT_TO_USER_MID);

        matrixService = MatrixServiceFactory.getInstance();
        roomService = matrixService.getRoomService();
    }

    @AfterEach
    public void teardown() {
        
        if(deleteRoomAfterTests){
            Map<String, List<String>> dmRooms = roomService.listBotDMRooms().getDirectRooms();
            dmRooms.remove(testAccountMatrixId);
            roomService.updateBotDMRoomList(dmRooms);

            for (String roomId: createdTestRooms) {
                roomService.leaveRoom(roomId);
            }
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

            markForDeletion(roomId);
        }

        @Test
        void shouldNotCreateADMIfADMWithUserExists() {
            String roomId1 = roomService.createDM(testAccountMatrixId);
            String roomId2 = roomService.createDM(testAccountMatrixId);

            Assertions.assertEquals(roomId1, roomId2);
            markForDeletion(roomId1);
        }
    }

    @Nested
    class SendingMessagesTest {
        @Test
        void shouldSendAMessageToADMRoom() {
            String roomId = roomService.createDM(testAccountMatrixId);
            Assertions.assertDoesNotThrow(() -> roomService.sendMessage(roomId, "Hello world"));
            markForDeletion(roomId);
        }

        @Test
        void shouldSendMultipleMessageToADMRoom() {
            String roomId = roomService.createDM(testAccountMatrixId);

            Assertions.assertDoesNotThrow(() -> roomService.sendMessage(roomId, "First message 1/3"));
            Assertions.assertDoesNotThrow(() -> roomService.sendMessage(roomId, "Second message 2/3"));
            Assertions.assertDoesNotThrow(() -> roomService.sendMessage(roomId, "Other message 3/3"));
            markForDeletion(roomId);
        }
    }

    /* 
    is it needed? same than shouldSendMultipleMessageToADMRoom
    @Nested
    class SendingOTPTest {
        @Test
        void shouldSendOTPCode() {
            String roomId =  matrixService.openDM(testAccountMatrixId);
            String serviceName = "TestApp";
            String otp = "123-456";
            Assertions.assertDoesNotThrow(() -> matrixService.sendMessage(roomId, "Voici votre code pour " + serviceName));
            Assertions.assertDoesNotThrow(() -> matrixService.sendMessage(roomId, otp));
            markForDeletion(roomId);
        }
    } */

    private void markForDeletion(String room) {
        createdTestRooms.add(room);
    }
}
