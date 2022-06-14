package org.beta.tchap.identite.matrix.rest;

import org.apache.log4j.BasicConfigurator;
import org.beta.tchap.identite.bot.BotSender;
import org.beta.tchap.identite.matrix.rest.homeserver.HomeServerService;
import org.beta.tchap.identite.matrix.rest.login.LoginService;
import org.beta.tchap.identite.matrix.rest.room.DirectRoomsResource;
import org.beta.tchap.identite.matrix.rest.room.RoomClient;
import org.beta.tchap.identite.matrix.rest.room.RoomClientFactory;
import org.beta.tchap.identite.matrix.rest.room.RoomService;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Map;

import static org.beta.tchap.identite.matrix.rest.homeserver.HomeServerService.buildHomeServerUrl;


// FIXME need cleanup of rooms before/after each test
// FIXME: errors to catch: feign.FeignException$Forbidden
class MatrixBotIntTest {
    private static RoomService roomService;
    private static BotSender botSender;
    private final static String testAccountMatrixId = "@calev.eliacheff-beta.gouv.fr:i.tchap.gouv.fr";

    @BeforeAll
    public static void setup() {
        // Needed for logging
        BasicConfigurator.configure();

        String account = Environment.getenv(Constants.TCHAP_ACCOUNT);
        String password = Environment.getenv(Constants.TCHAP_PASSWORD);

        HomeServerService homeServerService = new HomeServerService();
        LoginService loginService = new LoginService();

        String accountHomeServerUrl = buildHomeServerUrl(homeServerService.findHomeServerByEmail(account));
        String accessToken = loginService.findAccessToken(accountHomeServerUrl, account, password);

        RoomClient roomClient = RoomClientFactory.build(accountHomeServerUrl, accessToken);
        roomService = new RoomService(roomClient);

        botSender = new BotSender(roomService);
    }

    @AfterEach
    public void teardown() {
        Map<String, ArrayList<String>> dmRooms = roomService.listBotDMRooms().getDirectRooms();
        dmRooms.remove(testAccountMatrixId);
        roomService.updateBotDMRoomList(dmRooms);
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
        }
    }

    @Nested
    class SendingOTPTest {
        @Test
        void shouldSendOTPCode() {
            String otp = "1234";
            Assertions.assertDoesNotThrow(() -> botSender.sendOtp(otp, testAccountMatrixId));
        }
    }
}
