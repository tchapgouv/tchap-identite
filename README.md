# Authentification
Le projet Authentification otp publie un service d'authentification qui se base sur un envoi d'otp par email et par [Tchap](https://tchap.beta.gouv.fr/), la messagerie de l'Etat

## Monitoring

Realm uptime can be found here : https://updown.io/fta9

## Stack Technology
- maven : 3.8.2
- keycloak : 18.0
- quarkus : 2.7.5
- java : 11

## Run the local environment with docker containers

1. copy env.sample -> env, fill in passwords with (any value you want..)

```
cp .env.samble .env
````

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
