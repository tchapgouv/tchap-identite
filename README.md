# tchap-identite

keycloak : 18.0
quarkus : 2.7.5
java : 11

# docker image

1. from project https://github.com/MTES-MCT/keycloak-buildpack install keyclaok scalingo image

` docker build -t keycloak-scalingo . `

3. build custom providers

`mvn install`

3. launch containers

`docker compose up`

4. Connect to 
- keycloak admin http://localhost:18080 with admin/password
- email client : http://localhost:1080

4. install a openId client 
