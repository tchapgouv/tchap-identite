/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.keycloak.user;

import org.beta.authentification.matrix.rest.MatrixServiceFactory;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class TchapUserStorageFactory implements UserStorageProviderFactory<TchapUserStorage> {

    @Override
    public TchapUserStorage create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new TchapUserStorage(
                keycloakSession, componentModel, MatrixServiceFactory.getInstance());
    }

    @Override
    public String getId() {
        return "TchapUserStorageFactory";
    }
}
