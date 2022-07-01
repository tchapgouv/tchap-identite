package org.beta.tchap.identite.matrix.rest;

import org.apache.commons.lang.StringUtils;
import org.beta.tchap.identite.matrix.MatrixUserInfo;
import org.beta.tchap.identite.matrix.rest.homeserver.HomeServerService;
import org.beta.tchap.identite.matrix.rest.login.LoginService;
import org.beta.tchap.identite.matrix.rest.room.RoomClient;
import org.beta.tchap.identite.matrix.rest.room.RoomClientFactory;
import org.beta.tchap.identite.matrix.rest.room.RoomService;
import org.beta.tchap.identite.matrix.rest.user.UserInfoResource;
import org.beta.tchap.identite.matrix.rest.user.UserService;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;
import org.beta.tchap.identite.utils.LoggingUtilsFactory;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.beta.tchap.identite.matrix.rest.homeserver.HomeServerService.buildHomeServerUrl;

public class MatrixService {

    private static final Logger LOG = Logger.getLogger(MatrixService.class);

    private final HomeServerService homeServerService;
    private final UserService userService;
    private final RoomService roomService;

    private final String matrixId;


    protected MatrixService(String accountEmail, String tchapPassword) {

        LoginService loginService = new LoginService();
        homeServerService = new HomeServerService();

        String homeServer = homeServerService.findHomeServerByEmail(accountEmail);
        this.matrixId = UserService.emailToUserId(accountEmail, homeServer);

        String accountHomeServerUrl = buildHomeServerUrl(homeServer);
        String accessToken = loginService.findAccessToken(accountHomeServerUrl, accountEmail, tchapPassword);

        userService = new UserService(accountHomeServerUrl, accessToken);

        RoomClient roomClient = RoomClientFactory.build(accountHomeServerUrl, accessToken);
        roomService = new RoomService(roomClient, this.matrixId);
    }

    /*
     *
     * Check if an email is accepted on Tchap based on an hardcorded domain list
     * @param email
     * @return
     */
    public boolean isUserValid(String email) {
        if (LOG.isDebugEnabled()) {
            LOG.debugf(
                    "Check if email is valid in tchap : %s",
                    LoggingUtilsFactory.getInstance().logOrHash(email));
        }
        if (StringUtils.isEmpty(email)) {
            return false;
        }

        String userHomeServer = homeServerService.findHomeServerByEmail(email);
        boolean isValid = isEmailAcceptedOnTchap(userHomeServer);
        if (LOG.isDebugEnabled()) {
            LOG.debugf(
                    "Email[%s] is valid in tchap : %s",
                    LoggingUtilsFactory.getInstance().logOrHash(email), isValid);
        }
        return isValid;
    }

    /**
     * Get the home server of the user
     * @param email
     * @return (nullable) string of the homeserver
     */
    public String getUserHomeServer(String email){
        if (StringUtils.isEmpty(email)) {
            return null;
        }
        return homeServerService.findHomeServerByEmail(email);
    }

     /**
     * Check if the home server is accepted on tchap
     * @param userHomeServer
     * @return not null value
     */
    public boolean isHomeServerAcceptedOnTchap(String userHomeServer) {
        if (StringUtils.isEmpty(userHomeServer)) {
            return false;
        }
        boolean isValid = isEmailAcceptedOnTchap(userHomeServer);
        if (LOG.isDebugEnabled()) {
            LOG.debugf(
                    "HomeServer [%s] is valid in tchap : %s",
                    LoggingUtilsFactory.getInstance().logOrHash(userHomeServer), isValid);
        }
        return isValid;
    }

    /**
     * Check if an email is accepted on Tchap based on an hardcorded domain list
     * @param userHomeServer
     * @return
     */
    private boolean isEmailAcceptedOnTchap(String userHomeServer) {
        return !getInvalidHomeServers().contains(userHomeServer);
    }

    private List<String> getInvalidHomeServers() {
        String unauthorizedList = Environment.getenv(Constants.TCHAP_UNAUTHORIZED_HOME_SERVER_LIST);
        return StringUtils.isNotEmpty(unauthorizedList)
                ? Arrays.asList(unauthorizedList.split(","))
                : Collections.emptyList();
    }

    public RoomService getRoomService() {
        return roomService;
    }

    /**
     * Find Matrix User Informations with an email and its corresponding homeserver:
     * - matrixId
     * - valid : if an account has been created and still valid in Tchap
     *
     */
    public MatrixUserInfo findMatrixUserInfo(String userHomeServer, String email) {
        UserInfoResource userInfoByEmail = userService.findUserInfoByEmail(email, userHomeServer);
        if ( userInfoByEmail == null ){
            return new MatrixUserInfo(null,false);
        }
        boolean isValid = !userInfoByEmail.isDeactivated() && !userInfoByEmail.isExpired();
        return new MatrixUserInfo(userInfoByEmail.getUserId(),isValid);
    }

    /**
     * Return the matrixId of the connected user
     * @return not null string
     */
    public String getMatrixId(){
        return matrixId;
    }

}
