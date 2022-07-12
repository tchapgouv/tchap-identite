package org.beta.authentification.matrix.rest.homeserver.strategy;

import feign.RetryableException;
import org.apache.commons.lang.StringUtils;
import org.beta.authentification.matrix.exception.MatrixRuntimeException;
import org.beta.authentification.matrix.rest.homeserver.HomeServerClient;
import org.beta.authentification.matrix.rest.homeserver.HomeServerClientFactory;
import org.beta.authentification.matrix.rest.homeserver.HomeServerInfoQuery;
import org.beta.authentification.matrix.rest.homeserver.HomeServerInfoResource;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.beta.authentification.matrix.rest.homeserver.HomeServerService.buildHomeServerUrl;
/**
 * HealthyHomeServer strategy
 *
 * This strategy will elect a homeserver from a list of homeservers passing by the env : TCHAP_HOME_SERVER_LIST.
 * This homeserver will be used for API calls with health check and retry mecanism.
 * It will replace the homeserver in case of failure/
 *
 */
public class HealthyHomeServerStrategy implements HomeServerSelectionStrategy {
    private static final Logger LOG = Logger.getLogger(HealthyHomeServerStrategy.class);

    private final List<String> homeServerList;
    private final List<String> healthyHomeServerList;

    private HomeServerClient homeServerClient;

    public HealthyHomeServerStrategy(List<String> homeServerList) {
        this.homeServerList = homeServerList;
        healthyHomeServerList = new ArrayList<>(this.homeServerList);
        homeServerClient = getNewHealthyClient();
    }

    private HomeServerClient getNewHealthyClient() {
        while ( !healthyHomeServerList.isEmpty() ){
            String randomHomeServerName = getRandomHomeServerName();
            HomeServerClient candidate = HomeServerClientFactory.build(buildHomeServerUrl(randomHomeServerName));
            boolean isHealthy = validate(candidate, randomHomeServerName);
            if (isHealthy){
                LOG.debug("HomeServer will be used : "+ randomHomeServerName);
                return candidate;
            }
            else {
                healthyHomeServerList.remove(randomHomeServerName);
            }
        }
        throw new MatrixRuntimeException("No homeServer are available : " + StringUtils.join(homeServerList, ','));
    }

    private String getRandomHomeServerName() {
        return healthyHomeServerList.get(new Random().nextInt(healthyHomeServerList.size()));
    }

    private boolean validate(HomeServerClient homeServerClient, String homeServer){
        try {
            homeServerClient.healthCheck();
            return true;
        }
        catch (Exception exception){
            LOG.warnf("Cannot connect to homeServer : [%s] :", homeServer, exception.getMessage());
            if ( LOG.isDebugEnabled() ){
                LOG.debugf(exception,"Cannot connect to homeServer : [%s]", homeServer);
            }
            return false;
        }
    }

    @Override
    public String findHomeServerByEmail(String email) {
        String domain = HomeServerSelectionStrategy.getDomain(email);
        try {
            HomeServerInfoResource homeServerInfoResource =
                    homeServerClient.findHomeServerByEmail(new HomeServerInfoQuery("email", domain));
            return homeServerInfoResource.getHs();
        }
        catch (RetryableException exception){
            LOG.warnf("Cannot call findHomeServerByEmail(/api/info) : %s", exception.getMessage());
            if (LOG.isDebugEnabled()) {
                LOG.debugf(exception,"Cannot call findHomeServerByEmail(/api/info)");
            }
            homeServerClient = getNewHealthyClient();
            return this.findHomeServerByEmail(email);
        }
    }
}
