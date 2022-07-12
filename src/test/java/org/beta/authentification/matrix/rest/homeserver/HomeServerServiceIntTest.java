package org.beta.authentification.matrix.rest.homeserver;

import feign.RetryableException;
import org.beta.authentification.keycloak.utils.Constants;
import org.beta.authentification.keycloak.utils.Environment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HomeServerServiceIntTest {

    // integration test that will not work if the default strategy has randomly choose the homeserver 'i.tchap.gouv.fr'
    @Test
    void should_find_fail_with_default_strategy(){
        System.setProperty(Constants.FEATURE_HEALTHY_HOME_SERVER_STRATEGY, "false");
        List<String> homeServerList = Environment.strToList( "test.beta,test2.beta,i.tchap.gouv.fr,test3.beta");
        String email = "clark.kent@beta.gouv.fr";

        assertThrows(RetryableException.class, () -> {
            HomeServerService homeServerService = new HomeServerService(homeServerList);
            homeServerService.findHomeServerByEmail(email);
        });
    }


    @Test
    void should_find_success_with_healthy_homeserver_strategy(){
        System.setProperty(Constants.FEATURE_HEALTHY_HOME_SERVER_STRATEGY, "true");
        List<String> homeServerList = Environment.strToList( "test.beta,test2.beta,i.tchap.gouv.fr,test3.beta");

        assertDoesNotThrow(() ->
                new HomeServerService(homeServerList)
        );
    }

}