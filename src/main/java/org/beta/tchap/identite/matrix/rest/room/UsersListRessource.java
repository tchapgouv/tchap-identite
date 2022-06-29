package org.beta.tchap.identite.matrix.rest.room;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UsersListRessource {
    private Set<String> users;

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    public static UsersListRessource toUsersListRessource(Map<String, Object> rawResponse) {
        UsersListRessource users = new UsersListRessource();
        users.setUsers(new HashSet<>(rawResponse.keySet()));
        return users;
    }
}