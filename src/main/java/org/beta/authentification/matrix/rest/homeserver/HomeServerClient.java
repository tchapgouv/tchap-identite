/*
 * Copyright (c) 2022. DINUM
 * This file is licensed under the MIT License, see LICENSE.md
 */
package org.beta.authentification.matrix.rest.homeserver;

import feign.Headers;
import feign.QueryMap;
import feign.RequestLine;

public interface HomeServerClient {

    // https://matrix.i.tchap.gouv.fr/_matrix/identity/api/v1/info?medium=email&address=maghen.calinghee@gmail.com
    @RequestLine("GET /api/v1/info")
    @Headers("Content-Type: application/json")
    HomeServerInfoResource findHomeServerByEmail(@QueryMap HomeServerInfoQuery queryMap);
}
