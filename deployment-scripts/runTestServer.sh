fuser -k 9499/tcp || true
java -jar test-server/libs/server-0.0.1-SNAPSHOT.jar --server.port=9499 --spring.datasource.url=jdbc:mariadb://DB_URL --spring.datasource.username=DB_USERNAME --spring.datasource.password=DB_PASSWORD
