/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.matrix.rest.homeserver;

import org.beta.authentification.keycloak.utils.Features;
import org.beta.authentification.matrix.rest.homeserver.strategy.DefaultHomeServerStrategy;
import org.beta.authentification.matrix.rest.homeserver.strategy.HealthyHomeServerStrategy;
import org.beta.authentification.matrix.rest.homeserver.strategy.HomeServerSelectionStrategy;
import org.jboss.logging.Logger;

import java.util.List;

public class HomeServerService {
    private static final Logger LOG = Logger.getLogger(HomeServerService.class);
    private static final String HOME_SERVER_URL_PREFIX = "https://matrix";

    private final HomeServerSelectionStrategy strategy;

    public HomeServerService(List<String> homeServerList) {
        if(Features.isHomeServerSelectionStrategyEnabled()) {
            this.strategy = new HealthyHomeServerStrategy(homeServerList);
            LOG.debug("HomeServerSelectionStrategy : HealthyHomeServerStrategy (will select healthy home server and retry call if failure)");
        }
        else {
            this.strategy = new DefaultHomeServerStrategy(homeServerList);
            LOG.debug("HomeServerSelectionStrategy : DefaultHomeServerStrategy (will select one home server)");
        }
    }

    /**
     * Find the corresponding home server from an email address
     * @param email email address
     * @return Homeserver name
     */
    public String findHomeServerByEmail(String email) {
        return strategy.findHomeServerByEmail(email);
    }

    public static String buildHomeServerUrl(String homeServerName) {
        return HOME_SERVER_URL_PREFIX + "." + homeServerName;
    }

}
