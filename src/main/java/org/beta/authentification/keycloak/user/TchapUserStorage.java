/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.keycloak.user;

import java.util.HashMap;
import java.util.Map;

import org.beta.authentification.keycloak.utils.LoggingUtilsFactory;
import org.beta.authentification.matrix.rest.MatrixService;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.InMemoryUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;

public class TchapUserStorage implements UserStorageProvider, UserLookupProvider {

    private static final Logger LOG = Logger.getLogger(TchapUserStorage.class);
    protected KeycloakSession session;
    protected ComponentModel model;
    protected Map<String, UserModel> loadedUsers = new HashMap<>();
    private MatrixService matrixService;

    public static String ATTRIBUTE_HOMESERVER = "homeServer";

    /** Public constructor */
    public TchapUserStorage(
            KeycloakSession session, ComponentModel model, MatrixService matrixService) {
        this.session = session;
        this.model = model;
        this.matrixService = matrixService;
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        if (LOG.isDebugEnabled()) {
            // internal id of keycloak
            LOG.debugf(
                    "Checking keycloak id : %s", LoggingUtilsFactory.getInstance().logOrHash(id));
        }
        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();
        return getUserByUsername(realm, username);
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        if (LOG.isDebugEnabled()) {
            LOG.debugf(
                    "Checking username : %s",
                    LoggingUtilsFactory.getInstance().logOrHash(username));
        }
        UserModel user = loadedUsers.get(username);
        if (user == null) {
            String homeServer = matrixService.getUserHomeServer(username);
            if (matrixService.isHomeServerAcceptedOnTchap(homeServer)) {
                user = new InMemoryUserAdapter(session, realm, buildId(model, username));
                user.setEnabled(true);
                user.setUsername(username);
                user.setEmail(username);
                user.setSingleAttribute(ATTRIBUTE_HOMESERVER, homeServer);
                loadedUsers.put(username, user);
            }
        }

        return user;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        return getUserByUsername(realm, email);
    }

    /**
     * Defaults to 'f:' + storageProvider.getId() + ':' + getUsername()
     * https://www.keycloak.org/docs/latest/server_development/#storage-ids
     */
    private String buildId(ComponentModel model, String username) {
        return new StorageId(model.getId(), username).getId();
    }
    /*
     * no use
     */
    @Override
    public void preRemove(RealmModel realm) {
        UserStorageProvider.super.preRemove(realm);
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
        UserStorageProvider.super.preRemove(realm, group);
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
        UserStorageProvider.super.preRemove(realm, role);
    }

    @Override
    public void close() {}

    /*
     * Deprecated
     */
    @Override
    public UserModel getUserById(String s, RealmModel realmModel) {
        return getUserById(realmModel, s);
    }

    @Override
    public UserModel getUserByEmail(String s, RealmModel realmModel) {
        return getUserByEmail(realmModel, s);
    }

    @Override
    public UserModel getUserByUsername(String s, RealmModel realmModel) {
        return getUserByUsername(realmModel, s);
    }
}
