# tchap-identite

- keycloak : 18.0
- quarkus : 2.7.5
- java : 11

# docker image

0. copy env.sample -> env, fill in passwords with any value you want
- POSTGRES_PASSWORD= 
- KEYCLOAK_ADMIN_PASSWORD=
- KC_DB_PASSWORD=

2. from project https://github.com/MTES-MCT/keycloak-buildpack, install keycloak scalingo image

` docker build -t keycloak-scalingo . `

3. build custom providers

`mvn install`

3. launch containers

`docker compose up`

4. Connect to 
- keycloak admin http://localhost:8080 with admin/password
- email client : http://localhost:1080

4. install a openId client sample 

https://github.com/tchapgouv/oidc-client-example
