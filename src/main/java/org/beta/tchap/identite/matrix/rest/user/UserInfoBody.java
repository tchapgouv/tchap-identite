package org.beta.tchap.identite.matrix.rest.user;

import java.util.List;

public class UserInfoBody {
    private List<String> userIds;

    public UserInfoBody() {
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
