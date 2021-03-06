/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.keycloak.bot;

import org.apache.log4j.BasicConfigurator;
import org.beta.authentification.matrix.rest.room.DirectRoomsResource;
import org.beta.authentification.matrix.rest.room.RoomService;
import org.beta.authentification.matrix.rest.room.UsersListRessource;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashMap;

// FIXME need cleanup of rooms before/after each test
class MatrixBotUnitTest {
    private static FakeRoomClient roomClient;
    private static RoomService roomService;
    private static String testAccountMatrixId;

    @BeforeAll
    static void setup() {
        // Needed for logging
        BasicConfigurator.configure();
        testAccountMatrixId = "123";

        roomClient = new FakeRoomClient();
        roomClient.rooms = new HashMap<>();
        roomService = new RoomService(roomClient, testAccountMatrixId);
    }

    @AfterEach
    void teardown() {
        roomClient.rooms = new HashMap<>();
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
    class MembersInRoomTest {
        @Test
        void shouldFetchJoinUsers() {
            String roomId = roomService.createDM(testAccountMatrixId);

            UsersListRessource joinedMembers = roomService.getJoinedMembers(roomId);
            Assertions.assertEquals(0, joinedMembers.getUsers().size());
        }

        /*         @Test
        void shouldReturnFalseWhenRoomIsCreatedAndUserHasNotJoinYetOrHasLeave() {
            String roomId = roomService.createDM(testAccountMatrixId);

            boolean hasJoined = roomService.isInvitedUserInRoom(testAccountMatrixId, roomId);
            Assertions.assertFalse(hasJoined);
        } */
    }

    @Nested
    class CreateDMTest {
        @Test
        void shouldCreateADMAndAddDMEvent() {
            String roomId = roomService.createDM(testAccountMatrixId);

            DirectRoomsResource dmRooms = roomService.listBotDMRooms();
            Assertions.assertNotNull(roomId);
            Assertions.assertNotNull(dmRooms.getDirectRoomsForMId(testAccountMatrixId));
            Assertions.assertSame("123", dmRooms.getDirectRoomsForMId(testAccountMatrixId).get(0));
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
