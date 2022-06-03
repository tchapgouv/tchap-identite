package org.beta.tchap.identite.user;

import org.beta.tchap.identite.matrix.rest.MatrixService;
import org.beta.tchap.identite.utils.LoggingUtilsFactory;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

import java.util.HashMap;
import java.util.Map;

public class TchapUserStorage implements UserStorageProvider,
        UserLookupProvider {

    private static final Logger LOG = Logger.getLogger(TchapUserStorage.class);
    protected KeycloakSession session;
    protected ComponentModel model;
    protected Map<String, UserModel> loadedUsers = new HashMap<>();
    private MatrixService matrixService;

    /**
     * Public constructor
     */
    public TchapUserStorage(KeycloakSession session, ComponentModel model, MatrixService matrixService) {
        this.session = session;
        this.model = model;
        this.matrixService = matrixService;
    }


    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        if(LOG.isDebugEnabled()){
            //internal id of keycloak
            LOG.debugf("Checking keycloak id : %s", LoggingUtilsFactory.getInstance().logOrHash(id));
        }
        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();
        return getUserByUsername(realm, username);
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        if(LOG.isDebugEnabled()){
            LOG.debugf("Checking username : %s", LoggingUtilsFactory.getInstance().logOrHash(username));
        }
        UserModel adapter = loadedUsers.get(username);
        if (adapter == null && matrixService.isUserValid(username)) {
            adapter = new TchapUserModel(session, realm, model, username);
            loadedUsers.put(username, adapter);
        }

        return adapter;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        return getUserByUsername(realm, email);
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
    public void close() {
    }

    /*
     * Deprecated
     */
    @Override
    public UserModel getUserById(String s, RealmModel realmModel) {
        return getUserById(realmModel,s);
    }

    @Override
    public UserModel getUserByEmail(String s, RealmModel realmModel) {
        return getUserByEmail(realmModel,s);
    }

    @Override
    public UserModel getUserByUsername(String s, RealmModel realmModel) {
        return getUserByUsername(realmModel,s);
    }
}
