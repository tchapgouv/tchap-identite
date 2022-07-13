package org.beta.authentification.matrix.rest.homeserver.strategy;

import org.apache.commons.lang.StringUtils;

public interface HomeServerSelectionStrategy {
    String DOMAIN_SEPARATOR = "@";

    String findHomeServerByEmail(String email);

     static String getDomain(String email) {
        return DOMAIN_SEPARATOR + StringUtils.substringAfter(email, DOMAIN_SEPARATOR);
    }
}
