package org.beta.tchap.identite.bot;

import org.beta.tchap.identite.matrix.MatrixUserInfo;
import org.beta.tchap.identite.matrix.exception.MatrixRuntimeException;
import org.beta.tchap.identite.matrix.rest.MatrixService;
import org.beta.tchap.identite.user.TchapUserStorage;
import org.beta.tchap.identite.utils.Features;
import org.beta.tchap.identite.utils.LoggingUtilsFactory;
import org.jboss.logging.Logger;
import org.keycloak.models.UserModel;

public class BotSender {

    private static final Logger LOG = Logger.getLogger(BotSender.class);
    private final MatrixService matrixService;

    public BotSender(MatrixService matrixService) {
        this.matrixService = matrixService;
    }

    public boolean sendMessage(String serviceName, UserModel user, String friendlyCode) {
        if(Features.isTchapBotEnabled()) {
            String homeServer = user.getFirstAttribute(TchapUserStorage.ATTRIBUTE_HOMESERVER);
            MatrixUserInfo matrixUserInfo = matrixService.findMatrixUserInfo(homeServer, user.getUsername());
            if(!matrixUserInfo.isValid()){
                LOG.infof("User account is not valid on Tchap : %s", LoggingUtilsFactory.getInstance().logOrHide(user.getUsername()));
                return false;
            }

            String matrixId = matrixUserInfo.getMatrixId();
            if (LOG.isDebugEnabled()) {
                LOG.debugf("Sending OTP to tchap user: %s", LoggingUtilsFactory.getInstance().logOrHide(matrixId));
            }
            try {
                String roomId = matrixService.getRoomService().createDM(matrixId);
                matrixService.getRoomService().sendMessage(roomId, "Voici votre code pour " + serviceName);
                matrixService.getRoomService().sendMessage(roomId, friendlyCode);

            } catch (MatrixRuntimeException e) {
                LOG.errorf(
                        "Error while sending OTP to tchap user: %s", LoggingUtilsFactory.getInstance().logOrHide(matrixId));
                return false;
            }
        }
        return true;
    }
}
