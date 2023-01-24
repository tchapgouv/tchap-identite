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

For local development, the jar is copied to `/dev/providers`

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

## Troubleshooting

### SunCertPathBuilderException lors du build

L'erreur suivante peut se produire lors de l'exécution de la commande `mvn clean package` :

> DefaultHomeServerStrategyTest.should_get_healthy_client_success_with_one_home_server:16 » Retryable PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target executing GET https://matrix.i.tchap.gouv.fr/_matrix/identity/api/v1/info?address=%40beta.gouv.fr&medium=email

sudo keytool -import -alias tchap -keystore "/Library/Java/JavaVirtualMachines/jdk-19.jdk/Contents/Home/lib/security/cacerts" -file www.beta-sir.tchap.gouv.fr.cer

Il faut ajouter à Java le certificat de l'URL incriminée.

Importer le certificat avec Chrome (Firefox et Safari n'ont pas de bouton "exporter")

Pour savoir quelle JDK utilise Maven :
mvn -version

Il vous donne un "runtime" qui se termine par `Contents/Home`
Reprenez ce chemin runtime et ajoutez `/lib/security/cacerts`

Lancez la commande :

keytool -import -alias tchap -keystore "/opt/homebrew/Cellar/openjdk/19.0.1/libexec/openjdk.jdk/Contents/Home/lib/security/cacerts" -file www.beta-sir.tchap.gouv.fr.cer -storepass changeit

### Error response from daemon

Cette erreur peut se produire lors du lancement de `docker compose up`:

> Error response from daemon: Head "https://ghcr.io/v2/tchapgouv/keycloak-buildpack/manifests/master": unauthorized

On essaie d'accéder à un package sur la plateforme de github, mais pour cela il faut s'authentifier.

Tout d'abord, [créez un token d'accès "classique" à Github](https://docs.github.com/fr/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token#cr%C3%A9ation-dun-personal-access-token-classic). Donnez-lui simplement la permission `read:packages`.

Puis lancez la commande suivante pour vous connecter :

```
docker login ghcr.io --username <votre-login-github>
```

Lorsqu'un mot de passe vous est demandé, entrez le token qui a été généré ci-dessus.

Ensuite, le `docker compose up` fonctionne.
