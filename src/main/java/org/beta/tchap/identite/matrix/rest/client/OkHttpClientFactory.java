package org.beta.tchap.identite.matrix.rest.client;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import org.beta.tchap.identite.utils.Constants;
import org.beta.tchap.identite.utils.Environment;
import org.jboss.logging.Logger;

/** Factory that will build an OkHttpClient to enable ssl certificate validation */
public class OkHttpClientFactory {

    private static final Logger LOG = Logger.getLogger(OkHttpClientFactory.class);
    private static OkHttpClient instance;

    public static OkHttpClient getClient() {
        if (instance == null) {
            instance =
                    Boolean.parseBoolean(
                                    Environment.getenv(Constants.TCHAP_SKIP_CERTIFICATE_VALIDATION))
                            ? getUnsafeOkHttpClient()
                            : getSecuredClient();
        }
        return instance;
    }

    private static OkHttpClient getSecuredClient() {
        LOG.info("Initialize Secured HTTP Client");
        return new OkHttpClient();
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            LOG.warn(
                    "Initialize Unsecured HTTP Client (no ssl certificate validation is"
                            + " performed)");
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts =
                    new TrustManager[] {
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(
                                    java.security.cert.X509Certificate[] chain, String authType) {}

                            @Override
                            public void checkServerTrusted(
                                    java.security.cert.X509Certificate[] chain, String authType) {}

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[] {};
                            }
                        }
                    };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create a ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
