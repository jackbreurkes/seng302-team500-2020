fuser -k 8999/tcp || true
java -Dspring.profiles.active=prod -jar prod-server/libs/server-0.0.1-SNAPSHOT.jar --server.port=8999 --spring.datasource.url=jdbc:mariadb://DB_URL --spring.datasource.username=DB_USERNAME --spring.datasource.password=DB_PASSWORD  --super_admin_email=SUPER_ADMIN_EMAIL --super_admin_password=SUPER_ADMIN_PWD
