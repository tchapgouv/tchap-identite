package org.beta.authentification.keycloak.bot;

import org.beta.authentification.matrix.MatrixUserInfo;
import org.beta.authentification.matrix.exception.MatrixRuntimeException;
import org.beta.authentification.matrix.rest.MatrixService;
import org.beta.authentification.matrix.rest.room.DirectRoomsResource;
import org.beta.authentification.matrix.rest.room.RoomService;
import org.beta.authentification.matrix.rest.room.UsersListRessource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BotSenderTest {

    private static final String AN_SERVICE_NAME = "Audioconf";
    private static final String AN_EMAIL = "clark.kent@beta.gouv.fr";
    private static final String A_HOME_SERVER = "i.tchap.gouv.fr";
    private static final String A_MATRIX_ID = "@clark.kent-beta.gouv.fr:i.tchap.gouv.fr";
    private static final String A_CODE = "aaa-bbb";
    public static final String A_ROOM_ID = "!VtMCKXSSZTqkCnrZYX:i.tchap.gouv.fr";
    public static final String AN_ERROR = "An error";

    BotSender botSender;

    @Mock
    MatrixService matrixService;
    @Mock
    RoomService roomService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        botSender = new BotSender(matrixService);
    }

    @Test
    void send_message_successfully(){
        boolean isValid = true;
        boolean getUserHomeServerFails = false;
        boolean sendMessageFails = false;
        setupMocks(isValid, getUserHomeServerFails, sendMessageFails);

        assertDoesNotThrow(() ->botSender.sendMessage(AN_SERVICE_NAME, AN_EMAIL, A_CODE));

        verify(matrixService,times(1)).getUserHomeServer(eq(AN_EMAIL));
        verify(matrixService,times(1)).findMatrixUserInfo(A_HOME_SERVER, AN_EMAIL);
        // TODO : a high usage of RoomService might suggest to inject RoomService in BotSender directly
        verify(matrixService,times(4)).getRoomService();
        verify(roomService,times(1)).listBotDMRooms();
        verify(roomService,times(1)).getJoinedMembers(eq(A_ROOM_ID));
        verify(roomService,times(1)).sendMessage(eq(A_ROOM_ID),contains(A_CODE));
    }

    // invalid account should not be treated as an error as we could have user that has no tchap account
    @Test
    void send_message_successfully_on_illegible_account(){
        boolean isValid = false;
        boolean getUserHomeServerFails = false;
        boolean sendMessageFails = false;
        setupMocks(isValid, getUserHomeServerFails, sendMessageFails);

        assertDoesNotThrow(() ->botSender.sendMessage(AN_SERVICE_NAME, AN_EMAIL, A_CODE));

        verify(matrixService,times(1)).getUserHomeServer(eq(AN_EMAIL));
        verify(matrixService,times(1)).findMatrixUserInfo(A_HOME_SERVER, AN_EMAIL);
        verify(matrixService,times(0)).getRoomService();
        verify(roomService,times(0)).listBotDMRooms();
        verify(roomService,times(0)).getJoinedMembers(eq(A_ROOM_ID));
        verify(roomService,times(0)).sendMessage(eq(A_ROOM_ID),contains(A_CODE));
    }

    @Test
    void send_message_fail_on_get_user_home_server_failure(){
        boolean isValid = true;
        boolean getUserHomeServerFails = true;
        boolean sendMessageFails = false;
        setupMocks(isValid, getUserHomeServerFails, sendMessageFails);

        assertThrows(
                MatrixRuntimeException.class,
                () ->botSender.sendMessage(AN_SERVICE_NAME, AN_EMAIL, A_CODE),
                AN_ERROR
        );

        verify(matrixService,times(1)).getUserHomeServer(eq(AN_EMAIL));
        verify(matrixService,times(0)).findMatrixUserInfo(A_HOME_SERVER, AN_EMAIL);
        verify(matrixService,times(0)).getRoomService();
        verify(roomService,times(0)).listBotDMRooms();
        verify(roomService,times(0)).getJoinedMembers(eq(A_ROOM_ID));
        verify(roomService,times(0)).sendMessage(eq(A_ROOM_ID),contains(A_CODE));
    }

    @Test
    void send_message_fail_on_send_message_failure(){
        boolean isValid = true;
        boolean getUserHomeServerFails = false;
        boolean sendMessageFails = true;
        setupMocks(isValid, getUserHomeServerFails, sendMessageFails);

        assertThrows(
                MatrixRuntimeException.class,
                () ->botSender.sendMessage(AN_SERVICE_NAME, AN_EMAIL, A_CODE),
                AN_ERROR
        );

        verify(matrixService,times(1)).getUserHomeServer(eq(AN_EMAIL));
        verify(matrixService,times(1)).findMatrixUserInfo(A_HOME_SERVER, AN_EMAIL);
        verify(matrixService,times(4)).getRoomService();
        verify(roomService,times(1)).listBotDMRooms();
        verify(roomService,times(1)).getJoinedMembers(eq(A_ROOM_ID));
        verify(roomService,times(1)).sendMessage(eq(A_ROOM_ID),contains(A_CODE));
    }

    private void setupMocks(boolean isValid, boolean getUserHomeServerFails, boolean sendMessageFails) {
        if ( getUserHomeServerFails ){
            doThrow(new RuntimeException(AN_ERROR)).when(matrixService).getUserHomeServer(eq(AN_EMAIL));
        }
        else {
            doReturn(A_HOME_SERVER).when(matrixService).getUserHomeServer(eq(AN_EMAIL));
        }

        MatrixUserInfo matrixUserInfo = new MatrixUserInfo(A_MATRIX_ID, isValid);
        doReturn(matrixUserInfo).when(matrixService).findMatrixUserInfo(A_HOME_SERVER, AN_EMAIL);

        DirectRoomsResource allRooms = new DirectRoomsResource();
        HashMap<String, List<String>> directRooms = new HashMap<>();
        directRooms.put(A_MATRIX_ID, List.of(A_ROOM_ID));
        allRooms.setDirectRooms(directRooms);
        doReturn(allRooms).when(roomService).listBotDMRooms();

        UsersListRessource userListRessource = new UsersListRessource();
        HashSet<String> users = new HashSet<>();
        users.add(A_MATRIX_ID);
        userListRessource.setUsers(users);
        doReturn(userListRessource).when(roomService).getJoinedMembers(eq(A_ROOM_ID));

        if ( sendMessageFails ){
            doThrow(new RuntimeException(AN_ERROR)).when(roomService).sendMessage(eq(A_ROOM_ID),contains(A_CODE));
        }
        else {
            doNothing().when(roomService).sendMessage(eq(A_ROOM_ID),contains(A_CODE));
        }
        doReturn(roomService).when(matrixService).getRoomService();
    }

}