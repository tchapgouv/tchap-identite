# tchap-identite

- maven : 3.8.2
- keycloak : 18.0
- quarkus : 2.7.5
- java : 11

# docker image

0. copy env.sample -> env, fill in passwords with (any value you want..)
- POSTGRES_PASSWORD=password
- KEYCLOAK_ADMIN_PASSWORD=password
- KC_DB_PASSWORD=password
- TCHAP_ACCOUNT=service account for Tchap Identity created on Tchap Server side
- TCHAP_PASSWORD=password for the service account
- TCHAP_HOME_SERVER_LIST=list of home servers
- TCHAP_SKIP_CERTIFICATE_VALIDATION=false
- javax.net.ssl.trustStore=path to keystore
- javax.net.ssl.trustStorePassword=password of the keystore
- javax.net.ssl.trustStoreType=JKS

2. build extension with maven goal. A jar is produced with the custom providers and the custom view.  

`mvn package`

For local development, the jar is copied to /dev/providers

3. launch containers

`docker compose up`

4. Connect to 
- keycloak admin http://localhost:8080 with admin/password
  - in realm Tchap-identite, go to Clients>tchap-identite-client-sample>credentials> "Regenerate Secret"
  - copy this secret in your open id client (see -4-)
- email client : http://localhost:1080

4. install a openId client sample 

https://github.com/tchapgouv/oidc-client-example

