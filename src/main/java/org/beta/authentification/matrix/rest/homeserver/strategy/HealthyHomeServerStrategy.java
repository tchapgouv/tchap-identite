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
 * It will replace the homeserver in case of failure
 *
 */
public class HealthyHomeServerStrategy implements HomeServerSelectionStrategy {
    private static final Logger LOG = Logger.getLogger(HealthyHomeServerStrategy.class);

    private final List<String> healthyHomeServerList;
    private String randomHomeServerName;
    private HomeServerClient homeServerClient;

    public HealthyHomeServerStrategy(List<String> homeServerList) {
        healthyHomeServerList = new ArrayList<>(homeServerList);
        // Build new client with random homeserver
        randomHomeServerName = getRandomHomeServerName();
        homeServerClient = HomeServerClientFactory.build(buildHomeServerUrl(randomHomeServerName));
    }

    private String getRandomHomeServerName() {
        if (healthyHomeServerList.isEmpty()){
            throw new MatrixRuntimeException("No homeServer are available anymore");
        }
        return healthyHomeServerList.get(new Random().nextInt(healthyHomeServerList.size()));
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

            healthyHomeServerList.remove(randomHomeServerName);

            // Re-Build new client with random homeserver
            randomHomeServerName = getRandomHomeServerName();
            homeServerClient = HomeServerClientFactory.build(buildHomeServerUrl(randomHomeServerName));

            return this.findHomeServerByEmail(email);
        }
    }
}
