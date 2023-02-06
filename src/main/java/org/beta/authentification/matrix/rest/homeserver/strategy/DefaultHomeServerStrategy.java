package org.beta.authentification.matrix.rest.homeserver.strategy;

import org.beta.authentification.matrix.rest.homeserver.HomeServerClient;
import org.beta.authentification.matrix.rest.homeserver.HomeServerClientFactory;
import org.beta.authentification.matrix.rest.homeserver.HomeServerInfoQuery;
import org.beta.authentification.matrix.rest.homeserver.HomeServerInfoResource;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Random;

import static org.beta.authentification.matrix.rest.homeserver.HomeServerService.buildHomeServerUrl;

/**
 * DefaultHomeServer strategy
 *
 * This strategy will elect a homeserver from a list of homeservers passing by the env : TCHAP_HOME_SERVER_LIST.
 * This homeserver will be used for API calls but with no health check and no retry mecanism
 *
 */
public class DefaultHomeServerStrategy implements HomeServerSelectionStrategy {
    private static final Logger LOG = Logger.getLogger(DefaultHomeServerStrategy.class);

    private final List<String> homeServerList;
    private final HomeServerClient homeServerClient;

    public DefaultHomeServerStrategy(List<String> homeServerList) {
        this.homeServerList = homeServerList;
        
        homeServerClient = getClient();
    }

    private HomeServerClient getClient() {
        String randomHomeServerName = getRandomHomeServerName();
        HomeServerClient candidate = HomeServerClientFactory.build(buildHomeServerUrl(randomHomeServerName));
        LOG.debug("HomeServer will be used : "+ randomHomeServerName);
        return candidate;
    }

    private String getRandomHomeServerName() {
        return homeServerList.get(new Random().nextInt(homeServerList.size()));
    }

    @Override
    public String findHomeServerByEmail(String email) {
        String domain = HomeServerSelectionStrategy.getDomain(email);
        HomeServerInfoResource homeServerInfoResource =
                homeServerClient.findHomeServerByEmail(new HomeServerInfoQuery("email", domain));
        return homeServerInfoResource.getHs();
    }
}
