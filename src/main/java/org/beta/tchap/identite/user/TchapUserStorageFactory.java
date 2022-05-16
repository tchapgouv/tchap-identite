package org.beta.tchap.identite.user;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class TchapUserStorageFactory implements UserStorageProviderFactory<TchapUserStorage> {

    private static final Logger LOG = Logger.getLogger(TchapUserStorageFactory.class);

    @Override
    public TchapUserStorage create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new TchapUserStorage(keycloakSession, componentModel);
    }

    @Override
    public String getId() {
        return "TchapUserStorageFactory";
    }
}
