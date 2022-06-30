package org.beta.tchap.identite.matrix.rest;

import org.apache.log4j.BasicConfigurator;
import org.beta.tchap.TestSuiteUtils;
import org.beta.tchap.identite.matrix.rest.room.DirectRoomsResource;
import org.beta.tchap.identite.matrix.rest.room.RoomService;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class BotSenderTest {
    private static RoomService botRoomService;
    private static RoomService userTestRoomService;

    //private static MatrixService botMatrixService;
    private static String testAccountMatrixId;

    private final List<String> createdTestRooms = new ArrayList<>();
    private static Boolean deleteRoomAfterTests;

    @BeforeEach
    void setUp() {
        BasicConfigurator.configure();
        TestSuiteUtils.loadEnvFromDotEnvFile();

        deleteRoomAfterTests = Environment.getenv(TestSuiteUtils.ENV_DELETE_ROOM_AFTER_TESTS) == null
                || !Environment.getenv(TestSuiteUtils.ENV_DELETE_ROOM_AFTER_TESTS).equalsIgnoreCase("false");//todo refact this

        testAccountMatrixId = Environment.getenv(TestSuiteUtils.TEST_USER2_MATRIXID);

        String accountEmail = Environment.getenv(Constants.TCHAP_BOT_ACCOUNT_EMAIL);
        String password = Environment.getenv(Constants.TCHAP_BOT_PASSWORD);
        botRoomService = new MatrixService(accountEmail, password).getRoomService();

        String userTestAccountEmail = Environment.getenv(TestSuiteUtils.TEST_USER2_ACCOUNT);
        String userTestAccountPassword = Environment.getenv(TestSuiteUtils.TEST_USER2_PASSWORD);
        userTestRoomService = new MatrixService(userTestAccountEmail, userTestAccountPassword).getRoomService();
    }

    @AfterEach
    void tearDown() {
        waitAbit();
        if (deleteRoomAfterTests) {
            Map<String, List<String>> dmRooms = botRoomService.listBotDMRooms().getDirectRooms();
            dmRooms.remove(testAccountMatrixId);
            waitAbit();
            botRoomService.updateBotDMRoomList(dmRooms);

            createdTestRooms.forEach(roomId -> {
                waitAbit();
                botRoomService.leaveRoom(roomId);
            });
        }
    }

    @Nested
    class SendOTPMessageToUserTest {
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
    }


    private void markForDeletion(String room) {
        createdTestRooms.add(room);
    }

    private void waitAbit() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
