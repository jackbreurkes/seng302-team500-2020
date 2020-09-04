# Intitulada (SENG302 2020 Team 500)

Intitulada is a platform for sharing activities that other members can participate in.

### Contributors (alphabetically)
- Alex Hobson
- Jack van Heugten Breurkes
- James Matthew Auman
- Josh Yee
- Michael Freeman
- Michael Morgoun
- Olivia MacKintosh
- Riley Symon

### Basic Project Structure
- client/src Frontend source code (TypeScript - Vue)
- client/public publicly accesable web assets
- client/dist Frontend production build
- server/src Backend source code (Java - Spring)
- server/out Backend production build

### Super Admin credentials
- email: `super@admin.com`
- password: `IncludeActuals5348`
### Demo user credentials
- email: `tessa.testman@gmail.com`
- password: `password123`

### How to run
##### Client (Frontend/GUI)
1. `cd client`
2. `npm install`
3. `npm run serve`

Running on: http://localhost:9500/

##### Server (Backend/API)
1. `cd server`
2. `./gradlew bootRun`

Running on: http://localhost:9499/

### Accessing Deployed Application
- The production version of the application is accessable at [https://csse-s302g5.canterbury.ac.nz/prod](https://csse-s302g5.canterbury.ac.nz/prod)
- The test version of the application is accessable at [https://csse-s302g5.canterbury.ac.nz/test](https://csse-s302g5.canterbury.ac.nz/test)

### Project Dependencies
##### Client (Frontend/GUI)
- [vue v2.6.11](https://www.npmjs.com/package/vue/v/2.6.11)
- [vuetify v2.3.6](https://www.npmjs.com/package/vuetify/v/2.3.6)
- [axios v0.19.2](https://www.npmjs.com/package/axios/v/0.19.2)
- [core-js v3.6.4](https://www.npmjs.com/package/core-js/v/3.6.4)
- [serve v11.3.0](https://www.npmjs.com/package/serve/v/11.3.0)
- [vue-router v3.1.6](https://www.npmjs.com/package/vue-router/v/3.1.6)
- [vuejs-logger v1.5.4](https://www.npmjs.com/package/vuejs-logger/v/1.5.4)

These can be installed automatically with `npm install` while inside the client directory

##### Server (Backend/API)
- [spring-boot-starter-data-jpa](https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-data-jpa)
- [spring-boot-starter-data-rest](https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-data-rest)
- [spring-boot-starter-web](https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-web)
- [spring-boot-starter-security](https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-security)
- [mariadb-java-client v2.1.2](https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/2.1.2)

These can be installed automatically with `./gradlew --refresh-dependencies` while inside the server directory


### Todo
- Add team name into `build.gradle` and `package.json`

### Reference
- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#preface)
- [Vue docs](https://vuejs.org/v2/guide/)

### Additional Dependencies
- [Vue Router](https://router.vuejs.org/)
- [Vuetify](https://vuetifyjs.com/)
