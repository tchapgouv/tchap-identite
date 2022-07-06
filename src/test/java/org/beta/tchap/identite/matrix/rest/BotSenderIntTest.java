/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.tchap.identite.matrix.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.BasicConfigurator;
import org.beta.tchap.TestSuiteUtils;
import org.beta.tchap.identite.bot.BotSender;
import org.beta.tchap.identite.matrix.rest.room.DirectRoomsResource;
import org.beta.tchap.identite.matrix.rest.room.RoomService;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;
import org.junit.jupiter.api.*;

class BotSenderIntTest {
    private static RoomService botRoomService;
    private static RoomService userTestRoomService;
    private static BotSender botSender;

    // private static MatrixService botMatrixService;
    private static String testAccountMatrixId;

    private final List<String> createdTestRooms = new ArrayList<>();
    private static Boolean deleteRoomAfterTests;

    @BeforeEach
    void setUp() {
        BasicConfigurator.configure();
        TestSuiteUtils.loadEnvFromDotEnvFile();

        deleteRoomAfterTests =
                Environment.getenv(TestSuiteUtils.ENV_DELETE_ROOM_AFTER_TESTS) == null
                        || !Environment.getenv(TestSuiteUtils.ENV_DELETE_ROOM_AFTER_TESTS)
                                .equalsIgnoreCase("false"); // todo refact this

        testAccountMatrixId = Environment.getenv(TestSuiteUtils.TEST_USER2_MATRIXID);

        String accountEmail = Environment.getenv(Constants.TCHAP_BOT_ACCOUNT_EMAIL);
        String password = Environment.getenv(Constants.TCHAP_BOT_PASSWORD);
        MatrixService botMatrixService = new MatrixService(accountEmail, password);
        botRoomService = botMatrixService.getRoomService();

        String userTestAccountEmail = Environment.getenv(TestSuiteUtils.TEST_USER2_ACCOUNT);
        String userTestAccountPassword = Environment.getenv(TestSuiteUtils.TEST_USER2_PASSWORD);
        userTestRoomService =
                new MatrixService(userTestAccountEmail, userTestAccountPassword).getRoomService();

        botSender = new BotSender(botMatrixService);
    }

    @AfterEach
    void tearDown() {
        waitAbit();
        if (deleteRoomAfterTests) {
            Map<String, List<String>> dmRooms = botRoomService.listBotDMRooms().getDirectRooms();
            dmRooms.remove(testAccountMatrixId);
            waitAbit();
            botRoomService.updateBotDMRoomList(dmRooms);

            createdTestRooms.forEach(
                    roomId -> {
                        waitAbit();
                        botRoomService.leaveRoom(roomId);
                    });
        }
    }

    @Nested
    class SendOTPMessageToUserTest {
        @Test
        void shouldSendAnOTPToANewUser() {
            String userTestAccountEmail = Environment.getenv(TestSuiteUtils.TEST_USER2_ACCOUNT);

            boolean success = botSender.sendMessage("Lorem Ipsum", userTestAccountEmail, "123");
            String room = getRoomWithUser(testAccountMatrixId);
            boolean isUserInRoom = isInvitedUserInRoom(testAccountMatrixId, room);

            Assertions.assertTrue(success);
            Assertions.assertNotNull(room);
            Assertions.assertFalse(isUserInRoom);
            markForDeletion(room);
        }

        @Test
        void shouldNotCreateADMIfADMWithJoinedUserExists() {
            String userTestAccountEmail = Environment.getenv(TestSuiteUtils.TEST_USER2_ACCOUNT);

            boolean success1 = botSender.sendMessage("Lorem Ipsum", userTestAccountEmail, "123");
            String room1 = getRoomWithUser(testAccountMatrixId);
            userTestRoomService.join(room1);

            boolean success2 = botSender.sendMessage("Lorem Ipsum", userTestAccountEmail, "123");
            String room2 = getRoomWithUser(testAccountMatrixId);

            boolean isUserInRoom = isInvitedUserInRoom(testAccountMatrixId, room1);

            Assertions.assertTrue(success1);
            Assertions.assertTrue(success2);
            Assertions.assertTrue(isUserInRoom);
            assertNoNewRoomIsCreated(room1, room2);

            markForDeletion(room1);
            markForDeletion(room2);
        }

        @Test
        void shouldSendAnInviteAndNotCreateANewDMIfARoomExistsAndUserIsNotInRoom() {
            String userTestAccountEmail = Environment.getenv(TestSuiteUtils.TEST_USER2_ACCOUNT);

            botSender.sendMessage("Lorem Ipsum", userTestAccountEmail, "123");
            String room = getRoomWithUser(testAccountMatrixId);
            botSender.sendMessage("Lorem Ipsum", userTestAccountEmail, "123");

            // test invitation ?
            boolean isUserInRoom = isInvitedUserInRoom(testAccountMatrixId, room);

            Assertions.assertFalse(isUserInRoom);
            markForDeletion(room);
        }

        @Test
        void shouldSendAnInviteIfUserHasRefusedInvitation() {
            String userTestAccountEmail = Environment.getenv(TestSuiteUtils.TEST_USER2_ACCOUNT);

            boolean success1 = botSender.sendMessage("Lorem Ipsum", userTestAccountEmail, "123");
            String room1 = getRoomWithUser(testAccountMatrixId);
            // leaving without joining is the same as declining an invitation
            userTestRoomService.leaveRoom(room1);

            boolean success2 = botSender.sendMessage("Lorem Ipsum", userTestAccountEmail, "123");
            String room2 = getRoomWithUser(testAccountMatrixId);

            boolean isUserInRoom = isInvitedUserInRoom(testAccountMatrixId, room1);

            Assertions.assertTrue(success1);
            Assertions.assertTrue(success2);
            Assertions.assertFalse(isUserInRoom);
            assertNoNewRoomIsCreated(room1, room2);
            markForDeletion(room1);
            markForDeletion(room2);
        }

        @Test
        void shouldSendAnInviteIfUserHasLeaveTheRoom() {
            String userTestAccountEmail = Environment.getenv(TestSuiteUtils.TEST_USER2_ACCOUNT);

            boolean success1 = botSender.sendMessage("Lorem Ipsum", userTestAccountEmail, "123");
            String room1 = getRoomWithUser(testAccountMatrixId);
            userTestRoomService.join(room1);
            userTestRoomService.leaveRoom(room1);

            boolean success2 = botSender.sendMessage("Lorem Ipsum", userTestAccountEmail, "123");
            String room2 = getRoomWithUser(testAccountMatrixId);

            boolean isUserInRoom = isInvitedUserInRoom(testAccountMatrixId, room1);

            Assertions.assertTrue(success1);
            Assertions.assertTrue(success2);
            Assertions.assertFalse(isUserInRoom);
            Assertions.assertNotNull(room1);
            Assertions.assertEquals(room1, room2);
            markForDeletion(room1);
            markForDeletion(room2);
        }
    }

    void assertNoNewRoomIsCreated(String room1, String room2) {
        Assertions.assertNotNull(room1);
        Assertions.assertEquals(room1, room2);
    }

    private String getRoomWithUser(String destMatrixId) {
        DirectRoomsResource allRooms = botRoomService.listBotDMRooms();
        if (RoomService.hasARoomWithUser(destMatrixId, allRooms)) {
            return allRooms.getDirectRoomsForMId(destMatrixId).get(0);
        }
        return null;
    }

    private boolean isInvitedUserInRoom(String userMId, String roomId) {
        return botRoomService.getJoinedMembers(roomId).getUsers().contains(userMId);
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
