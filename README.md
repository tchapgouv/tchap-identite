# Authentification
Le projet Authentification otp publie un service d'authentification qui se base sur un envoi d'otp par email et par [Tchap](https://tchap.beta.gouv.fr/), la messagerie de l'Etat

## Monitoring

Realm uptime can be found here : https://updown.io/fta9

## Stack Technology

- maven : 3.8.2
- keycloak : 18.0
- quarkus : 2.7.5
- java : 11

## Exécution du projet en local avec des conteneurs docker

1. Créez le fichier `.env` à partir du fichier .env.sample

```
cp .env.sample .env
````

2. Construisez l'extension à l'aide de Maven. Un fichier `.jar` est créé dans `/dev/providers`.

```
mvn clean package
```

Pour préciser la version lors du run :
`mvn clean install -Drevision=X.Y.Z`


3. Lancez les conteneurs docker

```
docker compose up
```

4. L'admin keycloak est disponible à l'adresse http://localhost:8080 (admin/password), le client email est dispo à http://localhost:1080

5. Utilisation d'un client openID : 

- installez un client d'exemple openID (p. ex https://github.com/tchapgouv/oidc-client-example)
- dans l'admin keycloak, depuis le realm Tchap-identite, suivez Clients -> tchap-identite-client-sample -> credentials -> "Regenerate Secret"
- copiez ce secret dans votre client openID

6. After a modification in the java adapters, you need to compile the jar and restart keycloak. Indeed quarkus docker image does not support hot reload as of Keycloak 18 
```mvn clean package && docker compose restart keycloak```


## Code formatting

1. To format the code, execute the following command : `mvn spotless:apply`
2. You can view the diff with : `git diff `

## Troubleshooting

### SunCertPathBuilderException lors du build

L'erreur suivante peut se produire lors de l'exécution de la commande `mvn clean package` :

> DefaultHomeServerStrategyTest.should_get_healthy_client_success_with_one_home_server:16 » Retryable PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target executing GET https://matrix.i.tchap.gouv.fr/_matrix/identity/api/v1/info?address=%40beta.gouv.fr&medium=email


Il faut ajouter à Java le certificat de l'URL incriminée.

D'abord, [ouvrez l'URL avec Google Chrome](https://matrix.i.tchap.gouv.fr/_matrix/identity/api/v1/info?address=%40beta.gouv.fr&medium=email) et exportez le certificat (Firefox et Safari n'ont pas de bouton "exporter"). Vous obtenez un fichier `.cer` dont vous aurez besoin plus loin.

Lancez la commande suivante pour savoir quelle JDK utilise Maven :
```
mvn -version
```
Il vous donne un "runtime" qui se termine par `Contents/Home`. Reprenez ce chemin runtime et ajoutez-y `/lib/security/cacerts`.

Lancez la commande suivante pour ajouter le certificat à Java (remplacez "votre runtime java" par celui obtenu à l'exemple précédent). Dans l'exemple, le fichier certificat s'appelle `www.beta-sir.tchap.gouv.fr.cer` :

```
keytool -import -alias tchap -keystore "<votre runtime java>/lib/security/cacerts" -file www.beta-sir.tchap.gouv.fr.cer -storepass changeit
```

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

### duplicate key value violates unique constraint "uk_cli_scope"

Dans les logs docker compose, vous pouvez voir "tchap-identite-keycloak-1 exited with code 1" ce qui signifie qu'il y a eu un problème.

    tchap-identite-keycloak-1  | 2023-01-25 09:49:58,552 ERROR [org.keycloak.quarkus.runtime.cli.ExecutionExceptionHandler] (main) ERROR: could not execute statement
    tchap-identite-keycloak-1  | 2023-01-25 09:49:58,552 ERROR [org.keycloak.quarkus.runtime.cli.ExecutionExceptionHandler] (main) ERROR: ERROR: duplicate key value violates unique constraint "uk_cli_scope"
    tchap-identite-keycloak-1  |   Detail: Key (realm_id, name)=(c82a6531-6dd5-424e-ac9e-844ebd5e8f91, role_list) already exists.

Cela peut être dû à un problème lors de l'initialisation du conteneur. Une solution est de supprimer tous les conteneurs du projet, puis le volume de la base de données.

Pour lister les conteneurs docker :

```
docker ps --all
```

Pour les supprimer : `docker rm xxx yyy zzzz` où xxx, yyy et zzz sont les "container ID".

Pour supprimer le volume de la base de données :

```
docker volume rm tchap-identite_db_volume
```