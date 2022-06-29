package org.beta.tchap.identite.matrix.rest;

import org.apache.log4j.BasicConfigurator;
import org.beta.tchap.TestSuiteUtils;
import org.beta.tchap.identite.matrix.rest.MatrixService;
import org.beta.tchap.identite.matrix.rest.MatrixServiceFactory;
import org.beta.tchap.identite.matrix.rest.room.DirectRoomsResource;
import org.beta.tchap.identite.matrix.rest.room.RoomService;
import org.beta.tchap.identite.matrix.rest.room.UsersListRessource;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



// FIXME should test the events in the rooms to be sure it's ok
// FIXME: errors to catch: feign.FeignException$Forbidden
class MatrixBotIntTest {
    private static RoomService botRoomService;
    private static RoomService userTestRoomService;

    //private static MatrixService botMatrixService;
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
        
        MatrixService botMatrixService = new MatrixService(accountEmail, password);
        botRoomService = botMatrixService.getRoomService();

        String userTestAccountEmail = Environment.getenv(TestSuiteUtils.TEST_USER2_ACCOUNT);
        String userTestAccountPassword = Environment.getenv(TestSuiteUtils.TEST_USER2_PASSWORD);
        //String userTestMid = Environment.getenv(TestSuiteUtils.TEST_USER2_MATRIXID);
        userTestRoomService = new MatrixService(userTestAccountEmail, userTestAccountPassword).getRoomService();
        
    }

    @AfterEach
    public void teardown() {
        
        if(deleteRoomAfterTests){
            Map<String, List<String>> dmRooms = botRoomService.listBotDMRooms().getDirectRooms();
            dmRooms.remove(testAccountMatrixId);
            botRoomService.updateBotDMRoomList(dmRooms);

            for (String roomId: createdTestRooms) {
                botRoomService.leaveRoom(roomId);
            }
        }
    }

    @Nested
    class MembersInRoomTest {
        @Test
        void shouldFetchJoinUsers() {
            String roomId = botRoomService.createDM(testAccountMatrixId);

            UsersListRessource joinedMembers = botRoomService.getJoinedMembers(roomId);
            Assertions.assertEquals(0, joinedMembers.getUsers().size());

            markForDeletion(roomId);
        }

        @Test
        void shouldReturnFalseWhenRoomIsCreatedAndUserHasNotJoinYetOrHasLeave() {
            String roomId = botRoomService.createDM(testAccountMatrixId);

            boolean hasJoined = botRoomService.isInvitedUserInRoom(testAccountMatrixId, roomId);
            Assertions.assertFalse(hasJoined);

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
            String roomId = botRoomService.createDM(testAccountMatrixId);

            DirectRoomsResource dmRooms = botRoomService.listBotDMRooms();
            Assertions.assertNotNull(roomId);
            Assertions.assertNotNull(dmRooms.getDirectRoomsForMId(testAccountMatrixId));
            Assertions.assertTrue(dmRooms.getDirectRoomsForMId(testAccountMatrixId).size() > 0);

            markForDeletion(roomId);
        }

        @Test
        void shouldNotCreateADMIfADMWithUserExists() {
            String roomId1 = botRoomService.createDM(testAccountMatrixId);
            String roomId2 = botRoomService.createDM(testAccountMatrixId);
            
            markForDeletion(roomId1);
            Assertions.assertEquals(roomId1, roomId2);
        }

        @Test
        void should_invite_user_in_room() {
            String roomId = botRoomService.createDM(testAccountMatrixId);
            markForDeletion(roomId);

            Assertions.assertDoesNotThrow(() -> botRoomService.invite(roomId, testAccountMatrixId));
        }

        @Test
        void user_should_join_a_room_from_invite() {
            String roomId = botRoomService.createDM(testAccountMatrixId);

            Assertions.assertDoesNotThrow(() -> botRoomService.invite(roomId, testAccountMatrixId));
            Assertions.assertDoesNotThrow(() -> userTestRoomService.join(roomId));
            
            markForDeletion(roomId);
        }
      

        @Test
        void should_invite_user_if_user_has_left_the_room() {
            String roomId = botRoomService.createDM(testAccountMatrixId);

            //test_user join the room
            Assertions.assertDoesNotThrow(() -> userTestRoomService.join(roomId));

            //test_user account matrix leave the room
            Assertions.assertDoesNotThrow(() -> userTestRoomService.leaveRoom(roomId));
            
            //use botsender
        }
    }

    @Nested
    class SendingMessagesTest {
        @Test
        void shouldSendAMessageToADMRoom() {
            String roomId = botRoomService.createDM(testAccountMatrixId);
            Assertions.assertDoesNotThrow(() -> botRoomService.sendMessage(roomId, "Hello world"));
            markForDeletion(roomId);
        }

        @Test
        void shouldSendMultipleMessageToADMRoom() {
            String roomId = botRoomService.createDM(testAccountMatrixId);

            Assertions.assertDoesNotThrow(() -> botRoomService.sendMessage(roomId, "First message 1/3"));
            Assertions.assertDoesNotThrow(() -> botRoomService.sendMessage(roomId, "Second message 2/3"));
            Assertions.assertDoesNotThrow(() -> botRoomService.sendMessage(roomId, "Other message 3/3"));
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
