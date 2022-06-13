# tchap-identite
Le projet Tchap identité publie un service d'authentification qui utilise un canal tchap pour identifier les utilisateurs.

## Stack Technology
- maven : 3.8.2
- keycloak : 18.0
- quarkus : 2.7.5
- java : 11

## Run the local environment with docker containers

1. copy env.sample -> env, fill in passwords with (any value you want..)
- POSTGRES_PASSWORD=password
- KEYCLOAK_ADMIN_PASSWORD=password
- KC_DB_PASSWORD=password
- TCHAP_ACCOUNT=service account for Tchap Identity created on Tchap Server side
- TCHAP_PASSWORD=password for the service account
- TCHAP_HOME_SERVER_LIST=list of home servers that tchap-identity can connect to
- TCHAP_SKIP_CERTIFICATE_VALIDATION=false
- TCHAP_UNAUTHORIZED_HOME_SERVER_LIST=list of unauthorized home servers -- use to check if a user is valid
- TCHAP_OTP_MAIL_DELAY_IN_MINUTES= (optionnal) delay to wait to send another otp to users. default 0
- TCHAP_CODE_TIMEOUT_IN_MINUTES= (optionnal) validity of a otp code. default 60
- TCHAP_LOG_SENSITIVE_DATA = (optionnal) log sensitive data like username. default false

2. build extension with maven goal. A jar is produced with the custom providers and the custom view.

`mvn clean package`

to specify a version run :
`mvn clean install -Drevision=X.Y.Z`
For local development, the jar is copied to /dev/providers

3. launch containers

`docker compose up`

4. Connect to
- keycloak admin http://localhost:8080 with admin/password
- in realm Tchap-identite, go to Clients>tchap-identite-client-sample>credentials> "Regenerate Secret"
- copy this secret in your open id client (see -4-)
- email client : http://localhost:1080

5. install a openId client sample

https://github.com/tchapgouv/oidc-client-example


## Code formatting

1. To format the code, execute the following command : `mvn spotless:apply`
2. You can view the diff with : `git diff `