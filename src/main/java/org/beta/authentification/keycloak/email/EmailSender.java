/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.keycloak.email;

import java.util.*;

import org.beta.authentification.keycloak.utils.LoggingUtilsFactory;
import org.jboss.logging.Logger;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.ClientModel;
import org.keycloak.services.ServicesLogger;
/*
 * Singleton class
 */
public class EmailSender {
    private static final Logger LOG = Logger.getLogger(EmailSender.class);

    protected EmailSender() {}

    /**
     * Sends email with given code to user
     *
     * @return false if email sending doesn't happen
     */
    public boolean sendEmail(
            KeycloakSession session,
            RealmModel realm,
            UserModel user,
            ClientModel client,
            String code,
            String codeTimeout) {
        EmailTemplateProvider emailSender = session.getProvider(EmailTemplateProvider.class);
        emailSender.setRealm(realm);

        var result = true;
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debugf(
                        "send email to user %s",
                        LoggingUtilsFactory.getInstance().logOrHash(user.getUsername()));
            }
            emailSender.setUser(user);

            //use subject attributes to customize the subject of the email
            emailSender.send(
                    "login.code.email.title",
                    Collections.singletonList(client.getName()),
                    "loginCodeEmail.html",
                    createCodeLoginAttributes(code, codeTimeout, client));
        } catch (EmailException e) {
            ServicesLogger.LOGGER.failedToSendEmail(e);
            result = false;
        }
        return result;
    }

    private Map<String, Object> createCodeLoginAttributes(String loginCode, String codeTimeout, ClientModel client) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("loginCode", loginCode);
        attributes.put("codeTimeout", codeTimeout);
        attributes.put("clientName", client.getName());
        return attributes;
    }
}
