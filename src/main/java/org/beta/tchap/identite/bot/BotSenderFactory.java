package org.beta.tchap.identite.bot;

import org.beta.tchap.identite.email.EmailSender;
import org.beta.tchap.identite.matrix.rest.homeserver.HomeServerService;
import org.beta.tchap.identite.matrix.rest.login.LoginService;
import org.beta.tchap.identite.matrix.rest.room.RoomClient;
import org.beta.tchap.identite.matrix.rest.room.RoomFactory;
import org.beta.tchap.identite.matrix.rest.room.RoomService;
import org.beta.tchap.identite.matrix.rest.user.UserService;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;
import twitter4j.User;

import static org.beta.tchap.identite.matrix.rest.homeserver.HomeServerService.buildHomeServerUrl;

public class BotSenderFactory {
    private static BotSender instance;

    public static BotSender getInstance() {
        if ( instance == null ){
            String account = Environment.getenv(Constants.TCHAP_ACCOUNT);
            String password = Environment.getenv(Constants.TCHAP_PASSWORD);

            HomeServerService homeServerService = new HomeServerService();
            LoginService loginService = new LoginService();

            String accountHomeServerUrl = buildHomeServerUrl(homeServerService.findHomeServerByEmail(account));
            String accessToken = loginService.findAccessToken(accountHomeServerUrl, account, password);
            UserService userService = new UserService(accountHomeServerUrl, accessToken);

            RoomClient roomClient = RoomFactory.build(accountHomeServerUrl, accessToken);
            RoomService roomService = new RoomService(roomClient, userService);
            instance = new BotSender(roomService);
        }
        return instance;
    }
}
