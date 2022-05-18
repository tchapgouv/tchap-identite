# tchap-identite

- keycloak : 18.0
- quarkus : 2.7.5
- java : 11

# docker image

0. copy env.sample -> env, fill in passwords with (any value you want..)
- POSTGRES_PASSWORD=password
- KEYCLOAK_ADMIN_PASSWORD=password
- KC_DB_PASSWORD=password
- TCHAP_IDENTITY_ACCOUNT=service account for Tchap Identity created on Tchap Server side
- TCHAP_IDENTITY_PASSWORD=password

2. from project https://github.com/MTES-MCT/keycloak-buildpack, install keycloak scalingo image locally

` docker build -t keycloak-scalingo . `

3. build extension with maven goal. A jar is produced with the custom providers and the custom view.  

`mvn install`

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

