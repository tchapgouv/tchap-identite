package org.beta.tchap.identite.matrix.rest.homeserver;

import feign.Headers;
import feign.QueryMap;
import feign.RequestLine;

public interface HomeServerClient {

    // https://matrix.i.tchap.gouv.fr/_matrix/identity/api/v1/info?medium=email&address=maghen.calinghee@gmail.com
    @RequestLine("GET /info")
    @Headers("Content-Type: application/json")
    HomeServerInfoResource findHomeServerByEmail(@QueryMap HomeServerInfoQuery queryMap);
}
