package org.beta.tchap.identite.email;

import org.beta.tchap.identite.user.TchapUserModel;
import org.beta.tchap.identite.utils.LoggingUtilsFactory;
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
    private static final Logger LOG = Logger.getLogger(EmailSender.class);

    protected EmailSender(){}

    /**
     * Sends email with given code to user
     *
     * @return false if email sending doesn't happen
     */
    public boolean sendEmail(KeycloakSession session, RealmModel realm, UserModel user, String code, String codeTimeout)
    {
        EmailTemplateProvider emailSender = session.getProvider(EmailTemplateProvider.class);
        emailSender.setRealm(realm);
    
        var result = true;
        try {
            if(LOG.isDebugEnabled()){
                LOG.debugf("send email to user %s", LoggingUtilsFactory.getInstance().logOrHash(user.getUsername()));
            }
            //todo: remove this workaround
            emailSender.setUser(new TchapUserModel(null,null,null,user.getUsername()));
            emailSender.send("Confirmez la réservation de votre conférence audio", "loginCodeEmail.html",
                    createCodeLoginAttributes(code,codeTimeout));
        } catch (EmailException e) {
            ServicesLogger.LOGGER.failedToSendEmail(e);
            result = false;
        }
        return result;
    }

    private Map<String, Object> createCodeLoginAttributes(String loginCode, String codeTimeout)
    {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("loginCode", loginCode);
        attributes.put("codeTimeout", codeTimeout);
        return attributes;
    }
}
