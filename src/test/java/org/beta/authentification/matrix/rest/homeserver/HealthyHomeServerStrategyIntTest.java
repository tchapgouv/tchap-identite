package org.beta.authentification.matrix.rest.homeserver;

import org.beta.authentification.keycloak.utils.Environment;
import org.beta.authentification.matrix.exception.MatrixRuntimeException;
import org.beta.authentification.matrix.rest.homeserver.strategy.HealthyHomeServerStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HealthyHomeServerStrategyIntTest {

    @Test
    void should_get_healthy_client_success_with_one_home_server(){
        String homeServerList = "i.tchap.gouv.fr";

        HealthyHomeServerStrategy healthyHomeServerStrategy = new HealthyHomeServerStrategy(Environment.strToList(homeServerList));

        String homeServerName = healthyHomeServerStrategy.findHomeServerByEmail("clark.kent@beta.gouv.fr");
        assertNotNull(homeServerName);
        assertEquals("i.tchap.gouv.fr",homeServerName);
    }

    @Test
    void should_get_healthy_client_success_with_several_home_servers(){
        String homeServerList = "test.beta,test2.beta,i.tchap.gouv.fr,test3.beta";

        HealthyHomeServerStrategy healthyHomeServerStrategy = new HealthyHomeServerStrategy(Environment.strToList(homeServerList));

        String homeServerName = healthyHomeServerStrategy.findHomeServerByEmail("clark.kent@beta.gouv.fr");
        assertNotNull(homeServerName);
        assertEquals("i.tchap.gouv.fr",homeServerName);
    }

    @Test
    void should_get_healthy_client_fail_with_no_home_server(){
        String homeServerList = "";
        assertThrows(MatrixRuntimeException.class,() -> {
            new HealthyHomeServerStrategy(Environment.strToList(homeServerList));
        });
    }


    @Test
    void should_get_healthy_client_fail_with_unavailable_home_server(){
        String homeServerList = "test.beta,test2.beta,test3.beta";
        assertThrows(MatrixRuntimeException.class,() -> {
            new HealthyHomeServerStrategy(Environment.strToList(homeServerList));
        });
    }

}