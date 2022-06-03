package org.beta.tchap.identite.user;

import org.beta.tchap.identite.matrix.rest.MatrixServiceFactory;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class TchapUserStorageFactory implements UserStorageProviderFactory<TchapUserStorage> {

    @Override
    public TchapUserStorage create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new TchapUserStorage(keycloakSession, componentModel, MatrixServiceFactory.getInstance());
    }

    @Override
    public String getId() {
        return "TchapUserStorageFactory";
    }
}
