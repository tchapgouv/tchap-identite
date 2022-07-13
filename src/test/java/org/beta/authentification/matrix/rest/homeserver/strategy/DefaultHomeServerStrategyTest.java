package org.beta.authentification.matrix.rest.homeserver.strategy;

import feign.RetryableException;
import org.beta.authentification.keycloak.utils.Environment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultHomeServerStrategyTest {

    @Test
    void should_get_healthy_client_success_with_one_home_server(){
        String homeServerList = "i.tchap.gouv.fr";
        DefaultHomeServerStrategy healthyHomeServerStrategy = new DefaultHomeServerStrategy(Environment.strToList(homeServerList));

        String homeServerName = healthyHomeServerStrategy.findHomeServerByEmail("clark.kent@beta.gouv.fr");

        assertNotNull(homeServerName);
        assertEquals("i.tchap.gouv.fr",homeServerName);
    }

    @Test
    void should_get_healthy_client_fail_with_unavailable_home_server() {
        String homeServerList = "test.beta,test2.beta,test3.beta";
        DefaultHomeServerStrategy healthyHomeServerStrategy = new DefaultHomeServerStrategy(Environment.strToList(homeServerList));
        String email = "clark.kent@beta.gouv.fr";

        assertThrows(RetryableException.class,() -> healthyHomeServerStrategy.findHomeServerByEmail(email));
    }

}