package org.beta.tchap.identite.bot;

import org.beta.tchap.identite.matrix.MatrixUserInfo;
import org.beta.tchap.identite.matrix.exception.MatrixRuntimeException;
import org.beta.tchap.identite.matrix.rest.MatrixService;
import org.beta.tchap.identite.matrix.rest.room.DirectRoomsResource;
import org.beta.tchap.identite.matrix.rest.room.RoomService;
import org.beta.tchap.identite.utils.LoggingUtilsFactory;
import org.jboss.logging.Logger;

public class BotSender {

    private static final Logger LOG = Logger.getLogger(BotSender.class);
    private final MatrixService matrixService;

    public BotSender(MatrixService matrixService) {
        this.matrixService = matrixService;
    }

    /**
     * Send a otp code via a tchap bot
     * @param serviceName
     * @param username
     * @param friendlyCode
     * @return true if code has been sent successfully
     * @throws MatrixRuntimeException if message is not sent 
     */
    public boolean sendMessage(String serviceName, String username, String friendlyCode) {
        try{
            String homeServer = matrixService.getUserHomeServer(username);
            MatrixUserInfo matrixUserInfo = matrixService.findMatrixUserInfo(homeServer, username);
            if(!matrixUserInfo.isValid()){
                LOG.infof("User account is not valid on Tchap : %s", LoggingUtilsFactory.getInstance().logOrHide(username));
                return false;
            }

            String matrixId = matrixUserInfo.getMatrixId();
            if (LOG.isDebugEnabled()) {
                LOG.debugf("Prepare sending OTP to tchap user: %s", LoggingUtilsFactory.getInstance().logOrHide(matrixId));
            }
            String roomId = ensureUserIsInRoom(matrixId);
            if(roomId ==null ){
                roomId = matrixService.getRoomService().createDM(matrixId);
            }

            matrixService.getRoomService().sendMessage(roomId, "Voici votre code pour " + serviceName);
            matrixService.getRoomService().sendMessage(roomId, friendlyCode);

            return true;

        }catch(RuntimeException e){
            throw new MatrixRuntimeException(e);
        }
    }

    /**
     * Ensure there is a room where user is invited, if room existed, invitation is sent
     * @param destMatrixId
     * @return roomId or null if no existing room
     */
    private String ensureUserIsInRoom(String destMatrixId) {
        DirectRoomsResource allRooms = this.matrixService.getRoomService().listBotDMRooms();
        if (RoomService.hasARoomWithUser(destMatrixId, allRooms)) {
            String roomWithUser = allRooms.getDirectRoomsForMId(destMatrixId).get(0);
            if (!isInvitedUserInRoom(destMatrixId, roomWithUser)) {
                this.matrixService.getRoomService().invite(roomWithUser, destMatrixId);
            }
            return roomWithUser;
        }
        return null;
    }

    private boolean isInvitedUserInRoom(String userMId, String roomId) {
        return this.matrixService.getRoomService().getJoinedMembers(roomId).getUsers().contains(userMId);
    }
}
