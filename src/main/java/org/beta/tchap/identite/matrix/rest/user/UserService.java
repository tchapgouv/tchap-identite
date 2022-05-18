package org.beta.tchap.identite.matrix.rest.user;

import org.apache.commons.lang.StringUtils;
import org.beta.tchap.identite.matrix.rest.login.LoginResource;

import java.util.List;
import java.util.Map;

import static org.beta.tchap.identite.matrix.rest.MatrixService.MATRIX_HOME_SERVER;

public class UserService {
    private final UserClient userClient;
    public UserService(LoginResource loginResource) {
        this.userClient = UserClientFactory.build(loginResource);
    }

    public UserInfoResource findUserInfoByEmail(String email){
        UserInfoBody userInfoBody = new UserInfoBody();
        List<String> userIds = List.of(emailToUserId(email));
        userInfoBody.setUserIds(userIds);
        Map<String, Object> rawResponse = userClient.findByUsers(userInfoBody);
        return toUserInfoResource(userIds, rawResponse);
    }

    private UserInfoResource toUserInfoResource(List<String> userIds, Map<String, Object> rawResponse) {
        UserInfoResource userInfoResource = null;
        if (rawResponse.size() != 1 || userIds.size() != 1){
            return userInfoResource;
        }

        userInfoResource = new UserInfoResource();
        userInfoResource.setUserId(userIds.get(0));

        if (rawResponse.get(userIds.get(0)) instanceof Map){
            Map<String,Object> content = (Map<String, Object>) rawResponse.get(userIds.get(0));

            Boolean expired = (Boolean) getValue(content, "expired");
            userInfoResource.setExpired(Boolean.TRUE.equals(expired));

            Boolean deactivated = (Boolean) getValue(content, "deactivated");
            userInfoResource.setExpired(Boolean.TRUE.equals(deactivated));
        }

        return userInfoResource;
    }

    private Object getValue(Map<String, Object> content, String key) {
        if (content.containsKey(key)){
            return content.get(key);
        }
        return null;
    }

    public String emailToUserId(String email) {
        if (StringUtils.isEmpty(email)){
            return email;
        }
        return "@" + email.replace("@", "-") + ":" + MATRIX_HOME_SERVER;
    }
}
