package org.beta.tchap.identite.authenticator;

import org.beta.tchap.identite.email.EmailSenderFactory;
import org.beta.tchap.identite.matrix.rest.MatrixServiceFactory;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;
import org.beta.tchap.identite.utils.SecureCodeFactory;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Collections;
import java.util.List;

public class OtpLoginAuthenticatorFactory implements AuthenticatorFactory {

    public static final String ID = "tchap-otp-login";
    public static final String DISPLAY_TYPE = "Tchap Login OTP";
    public static final String HELP_TEXT = DISPLAY_TYPE;
    public static final String CATEGORY = "tchap";
    private static final int TCHAP_CODE_TIMEOUT_IN_MINUTES_DEFAULT = 60;
    private static final int TCHAP_OTP_MAIL_DELAY_IN_MINUTES_DEFAULT = 0;

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.DISABLED
    };

    private int codeTimeout;
    private int mailDelay;

    @Override
    public Authenticator create(KeycloakSession session) {
        return new OtpLoginAuthenticator(
                SecureCodeFactory.getInstance(),
                EmailSenderFactory.getInstance(),
                codeTimeout,
                mailDelay,
                MatrixServiceFactory.getInstance());
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getReferenceCategory() {
        return CATEGORY;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public String getDisplayType() {
        return DISPLAY_TYPE;
    }

    @Override
    public String getHelpText() {
        return HELP_TEXT;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }

    @Override
    public void init(Config.Scope config) {
        mailDelay =
                Environment.getenv(Constants.TCHAP_OTP_MAIL_DELAY_IN_MINUTES) != null
                        ? Integer.parseInt(
                        Environment.getenv(Constants.TCHAP_OTP_MAIL_DELAY_IN_MINUTES))
                        : TCHAP_OTP_MAIL_DELAY_IN_MINUTES_DEFAULT;

        codeTimeout =
                Environment.getenv(Constants.TCHAP_CODE_TIMEOUT_IN_MINUTES) != null
                        ? Integer.parseInt(
                        Environment.getenv(Constants.TCHAP_CODE_TIMEOUT_IN_MINUTES))
                        : TCHAP_CODE_TIMEOUT_IN_MINUTES_DEFAULT;
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // not needed for current version
    }

    @Override
    public void close() {
        // not used for current version
    }
}
