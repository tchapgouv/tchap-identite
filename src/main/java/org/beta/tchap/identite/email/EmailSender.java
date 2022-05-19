package org.beta.tchap.identite.email;

import org.beta.tchap.identite.user.TchapUserModel;
import org.beta.tchap.identite.user.TchapUserStorage;
import org.jboss.logging.Logger;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.ServicesLogger;

import java.util.HashMap;
import java.util.Map;

public class EmailSender
{
    private static final Logger LOG = Logger.getLogger(TchapUserStorage.class);

    protected EmailSender(){}

    /**
     * Sends email with given code to user
     *
     * @return false if email sending doesn't happen
     */
    public boolean sendEmail(KeycloakSession session, RealmModel realm, UserModel user, String code)
    {

        EmailTemplateProvider emailSender = session.getProvider(EmailTemplateProvider.class);
        emailSender.setRealm(realm);

        var result = true;
        try {
            LOG.infof("send email to user %s %s", user.getUsername(), user.getEmail());
            //todo: remove this workaround
            emailSender.setUser(new TchapUserModel(null,null,null,user.getUsername()));
            emailSender.send(code + " Login Code", "loginCode.ftl",
                    createCodeLoginAttributes(code));
        } catch (EmailException e) {
            ServicesLogger.LOGGER.failedToSendEmail(e);
            result = false;
        }
        return result;
    }

    private Map<String, Object> createCodeLoginAttributes(String loginCode)
    {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("loginCode", loginCode);
        return attributes;
    }
}
