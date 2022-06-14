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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.beta.tchap.identite.matrix.rest.homeserver.HomeServerService.buildHomeServerUrl;


// FIXME need cleanup of rooms before/after each test
class MatrixBotIntTest {
    private static RoomService roomService;
    private static BotSender botSender;

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

    @Nested
    class SendingOTPTest {
        @Test
        void shouldSendOTPCode() {
            String destId = "@calev.eliacheff-beta.gouv.fr:i.tchap.gouv.fr";
            String otp = "1234";
            Assertions.assertDoesNotThrow(() -> botSender.sendOtp(otp, destId));
        }
    }
}