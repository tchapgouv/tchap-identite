package org.beta.tchap.identite.user;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.adapter.AbstractUserAdapter;

public class TchapUserModel extends AbstractUserAdapter {

    private String email;

    public TchapUserModel(KeycloakSession session, RealmModel realm, ComponentModel model, String email){
        super(session, realm, model);
        this.email = email;
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public String getEmail(){
        return email;
    }
}
