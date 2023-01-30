/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.matrix.rest;

import static org.beta.authentification.matrix.rest.homeserver.HomeServerService.buildHomeServerUrl;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.beta.authentification.keycloak.utils.LoggingUtilsFactory;
import org.beta.authentification.matrix.rest.homeserver.HomeServerService;
import org.beta.authentification.matrix.rest.login.LoginService;
import org.beta.authentification.matrix.rest.room.RoomClient;
import org.beta.authentification.matrix.rest.room.RoomClientFactory;
import org.beta.authentification.matrix.rest.room.RoomService;
import org.beta.authentification.matrix.rest.user.UserInfoResource;
import org.beta.authentification.matrix.rest.user.UserService;
import org.jboss.logging.Logger;

/**
 * Scope request object
 */
public class MatrixService {

    private static final Logger LOG = Logger.getLogger(MatrixService.class);

    private final HomeServerService homeServerService;
    private final UserService userService;
    private final RoomService roomService;
    private final List<String> unauthorizedList;


    /**
     * This constructor is stateless. It does nothing else than building the service.
     * @param homeServerList
     * @param unauthorizedList
     */
    protected MatrixService(List<String> homeServerList, List<String> unauthorizedList) {
        homeServerService = new HomeServerService(homeServerList);
        roomService = null;
        userService = null;
        this.unauthorizedList = unauthorizedList;
        //todo matrix services should be splitted into two subvervices
    
    }

    /**
     * This constructor expects an access token. It does not call /login
     * @param accountEmail
     * @param homeServerList
     * @param unauthorizedList
     * @param accessToken
     */
    protected MatrixService( String accountEmail, List<String> homeServerList, List<String> unauthorizedList, String accessToken) {
        this.unauthorizedList = unauthorizedList;
        homeServerService = new HomeServerService(homeServerList);
        String homeServer = homeServerService.findHomeServerByEmail(accountEmail);

        String accountHomeServerUrl = buildHomeServerUrl(homeServer);

        userService = new UserService(accountHomeServerUrl, accessToken);

        RoomClient roomClient = RoomClientFactory.build(accountHomeServerUrl, accessToken);
        String matrixId = UserService.emailToUserId(accountEmail, homeServer);
        roomService = new RoomService(roomClient, matrixId);
        //todo matrix services should be splitted into two subvervices

    }


    /**
     *
     * Check if an email is accepted on Tchap based on a hardcorded domain list
     * @param email that we try to validate
     * @return true if email is accepted on Tchap otherwise false
     */
    public MatrixAutorizationInfo isEmailAuthorized(String email) {
        if (LOG.isDebugEnabled()) {
            LOG.debugf(
                    "Check if email is valid in tchap : %s",
                    LoggingUtilsFactory.getInstance().logOrHash(email));
        }
        if (StringUtils.isEmpty(email)) {
            return new MatrixAutorizationInfo(null,false);
        }

        String userHomeServer = homeServerService.findHomeServerByEmail(email);
        boolean isValid = isHomeServerAcceptedOnTchap(userHomeServer);
        MatrixAutorizationInfo result = new MatrixAutorizationInfo(userHomeServer, isValid);
        if (LOG.isDebugEnabled()) {
            LOG.debugf(
                    "Email[%s] - HomeServer[%s] is valid in tchap : %s",
                    LoggingUtilsFactory.getInstance().logOrHash(email),
                    userHomeServer,
                    result.isAuthorized());
        }
        return result;
    }

    /**
     * Check if the home server is accepted on tchap
     *
     * @param userHomeServer
     * @return not null value
     */
    private boolean isHomeServerAcceptedOnTchap(String userHomeServer) {
        return !unauthorizedList.contains(userHomeServer);
    }



    public RoomService getRoomService() {
        return roomService;
    }

    /**
     * Find Matrix User Informations with an email and its corresponding homeserver: - matrixId -
     * valid : if an account has been created and still valid in Tchap
     */
    public MatrixUserInfo findMatrixUserInfo(String userHomeServer, String email) {
        UserInfoResource userInfoByEmail = userService.findUserInfoByEmail(email, userHomeServer);
        if (userInfoByEmail == null) {
            return new MatrixUserInfo(null, false);
        }
        boolean isValid = !userInfoByEmail.isDeactivated() && !userInfoByEmail.isExpired();
        return new MatrixUserInfo(userInfoByEmail.getUserId(), isValid);
    }
}
