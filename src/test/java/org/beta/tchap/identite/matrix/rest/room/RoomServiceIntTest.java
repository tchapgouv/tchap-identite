package org.beta.tchap.identite.matrix.rest.room;

import org.apache.log4j.BasicConfigurator;
import org.beta.tchap.TestSuiteUtils;
import org.beta.tchap.identite.matrix.exception.MatrixRuntimeException;
import org.beta.tchap.identite.matrix.rest.MatrixService;
import org.beta.tchap.identite.matrix.rest.MatrixServiceUtil;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



// FIXME should test the events in the rooms to be sure it's ok
class RoomServiceIntTest {
    private static RoomService botRoomService;
    private static RoomService userTestRoomService;

    private static MatrixService botMatrixService;
    private static String testAccountMatrixId;

    private final List<String> createdTestRooms = new ArrayList<>();
    private static Boolean deleteRoomAfterTests;
    @BeforeAll
    public static void setup() {
        // Needed for logging
        BasicConfigurator.configure();
        TestSuiteUtils.loadEnvFromDotEnvFile();
        
        deleteRoomAfterTests = Environment.getenv(TestSuiteUtils.ENV_DELETE_ROOM_AFTER_TESTS) == null 
        || !Environment.getenv(TestSuiteUtils.ENV_DELETE_ROOM_AFTER_TESTS).toLowerCase().equals("false");//todo refact this

        testAccountMatrixId = Environment.getenv(TestSuiteUtils.TEST_USER2_MATRIXID);
        
        String accountEmail = Environment.getenv(Constants.TCHAP_BOT_ACCOUNT_EMAIL);
        String password = Environment.getenv(Constants.TCHAP_BOT_PASSWORD);
        //String matrixId = Environment.getenv(Constants.TCHAP_BOT_MATRIX_ID);
        
        botMatrixService = MatrixServiceUtil.getMatrixService(accountEmail, password);
        botRoomService = botMatrixService.getRoomService();
        
        String userTestAccountEmail = Environment.getenv(TestSuiteUtils.TEST_USER2_ACCOUNT);
        String userTestAccountPassword = Environment.getenv(TestSuiteUtils.TEST_USER2_PASSWORD);
        //String userTestMid = Environment.getenv(TestSuiteUtils.TEST_USER2_MATRIXID);
        userTestRoomService = MatrixServiceUtil.getMatrixService(userTestAccountEmail, userTestAccountPassword).getRoomService();
        
    }

    @AfterEach
    public void teardown() {
        waitAbit();

        if(deleteRoomAfterTests){
            Map<String, List<String>> dmRooms = botRoomService.listBotDMRooms().getDirectRooms();
            waitAbit();
            dmRooms.remove(testAccountMatrixId);
            botRoomService.updateBotDMRoomList(dmRooms);

            for (String roomId: createdTestRooms) {
                waitAbit();
                botRoomService.leaveRoom(roomId);
            }
        }
    }

    @Nested
    class MembersInRoomTest {
        @Test
        void shouldFetchJoinUsers() {
            String roomId = botRoomService.createDM(testAccountMatrixId);
            waitAbit();
            UsersListRessource joinedMembers = botRoomService.getJoinedMembers(roomId);
            Assertions.assertEquals(1, joinedMembers.getUsers().size());
            Assertions.assertTrue(joinedMembers.getUsers().contains(botMatrixService.getMatrixId()));

            markForDeletion(roomId);
        }
    }

    @Nested
    class NoEventsTest {
        
        @Test
        void shouldInitiazeMatrixService() {}
        
        
        @Test
        void shouldHaveNoDMEventsIfNoDM() {
            DirectRoomsResource dmRooms = botRoomService.listBotDMRooms();
            Assertions.assertNull(dmRooms.getDirectRoomsForMId(testAccountMatrixId));
        }
    }

    @Nested
    class CreateDMTest {
        @Test
        void shouldCreateADMAndAddDMEvent() {
            waitAbit();
            String roomId = botRoomService.createDM(testAccountMatrixId);
            waitAbit();
            DirectRoomsResource dmRooms = botRoomService.listBotDMRooms();
            Assertions.assertNotNull(roomId);
            Assertions.assertNotNull(dmRooms.getDirectRoomsForMId(testAccountMatrixId));
            Assertions.assertTrue(dmRooms.getDirectRoomsForMId(testAccountMatrixId).size() > 0);

            markForDeletion(roomId);
        }

        @Test
        void shouldThrowIAE_whenUserIdIsNull() {
            waitAbit();
            Assertions.assertThrows(IllegalArgumentException.class, () -> botRoomService.createDM(null));
        }


        @Test
        void shouldUpdateRoomAccountData() {
            waitAbit();
            String roomId = "room1";
            String userMid = "user1";
            botRoomService.updateRoomAccounData(userMid, roomId);
            Assertions.assertTrue(botRoomService.listBotDMRooms().getDirectRoomsForMId(userMid).contains(roomId));
        }

        @Test
        void should_throw_IAE() {
            waitAbit();
            String roomId = null;
            String userMid = "user1";
            Assertions.assertThrows(IllegalArgumentException.class, () -> botRoomService.updateRoomAccounData(userMid, roomId)); 
        }

        @Test
        void should_throw_IAE2() {
            waitAbit();
            String roomId = "room1";
            String userMid = null;
            Assertions.assertThrows(IllegalArgumentException.class, () -> botRoomService.updateRoomAccounData(userMid, roomId)); 
        }
    }
    @Nested
    class InviteTest {

        @Test
        void reinvite_user_in_dm() {
            String roomId = botRoomService.createDM(testAccountMatrixId);
            markForDeletion(roomId);
            Assertions.assertDoesNotThrow(() -> botRoomService.invite(roomId, testAccountMatrixId));
        }

        @Test
        void should_throw_if_user_already_in_room() {
            String roomId = botRoomService.createDM(testAccountMatrixId);
            markForDeletion(roomId);
            userTestRoomService.join(roomId);
            Assertions.assertThrows(MatrixRuntimeException.class, () -> botRoomService.invite(roomId, testAccountMatrixId));
        }
      
    }

    @Nested
    class SendingMessagesTest {
        @Test
        void shouldSendAMessageToADMRoom() {
            String roomId = botRoomService.createDM(testAccountMatrixId);
            markForDeletion(roomId);
            botRoomService.sendMessage(roomId, "Hello world");
        }

        @Test
        void shouldSendMultipleMessageToADMRoom() {
            String roomId = botRoomService.createDM(testAccountMatrixId);
            waitAbit();
            Assertions.assertDoesNotThrow(() -> botRoomService.sendMessage(roomId, "First message 1/3"));
            Assertions.assertDoesNotThrow(() -> botRoomService.sendMessage(roomId, "Second message 2/3"));
            waitAbit();
            Assertions.assertDoesNotThrow(() -> botRoomService.sendMessage(roomId, "Other message 3/3"));
            markForDeletion(roomId);
        }
        @Test
        void should_throw_IAE() {
            waitAbit();
            String roomId = null;
            String message = "message";
            Assertions.assertThrows(IllegalArgumentException.class, () -> botRoomService.sendMessage(roomId, message)); 
        }

        @Test
        void should_throw_IAE2() {
            waitAbit();
            String roomId = "room1";
            String message = null;
            Assertions.assertThrows(IllegalArgumentException.class, () -> botRoomService.updateRoomAccounData(roomId, message)); 
        }
    }


    private void markForDeletion(String room) {
        createdTestRooms.add(room);
    }

    private void waitAbit(){
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
