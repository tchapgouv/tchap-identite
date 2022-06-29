package org.beta.tchap.identite.matrix.rest.user;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class UserService {
    private final UserClient userClient;

    public UserService(String homeServerUrl, String accessToken) {
        userClient = UserClientFactory.build(homeServerUrl, accessToken);
    }

    public UserInfoResource findUserInfoByEmail(String email, String homeServer) {
        UserInfoBody userInfoBody = new UserInfoBody();
        List<String> userIds = List.of(emailToUserId(email, homeServer));
        userInfoBody.setUserIds(userIds);
        Map<String, Object> rawResponse = userClient.findByUsers(userInfoBody);
        return toUserInfoResource(userIds, rawResponse);
    }

    private UserInfoResource toUserInfoResource(
            List<String> userIds, Map<String, Object> rawResponse) {
        UserInfoResource userInfoResource = null;
        if (rawResponse.size() != 1 || userIds.size() != 1) {
            return userInfoResource;
        }

        userInfoResource = new UserInfoResource();
        userInfoResource.setUserId(userIds.get(0));

        if (rawResponse.get(userIds.get(0)) instanceof Map) {
            Map<String, Object> content = (Map<String, Object>) rawResponse.get(userIds.get(0));

            Boolean expired = (Boolean) getValue(content, "expired");
            userInfoResource.setExpired(Boolean.TRUE.equals(expired));

            Boolean deactivated = (Boolean) getValue(content, "deactivated");
            userInfoResource.setExpired(Boolean.TRUE.equals(deactivated));
        }

        return userInfoResource;
    }

    private Object getValue(Map<String, Object> content, String key) {
        if (content.containsKey(key)) {
            return content.get(key);
        }
        return null;
    }

    public static String emailToUserId(String email, String homeServer) {
        if (StringUtils.isEmpty(email)) {
            return email;
        }
        return "@" + email.replace("@", "-") + ":" + homeServer;
    }
}
