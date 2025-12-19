package at.campus.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/*

Сделать admin endpoints (change role / disable / enable / lock / unlock)

Сделать endpoint смены пароля (для текущего пользователя)

(опционально, но важно) кеширование UserDetails, чтобы не бить БД на каждый запрос

Login на неверный пароль → 500 (надо ловить AuthenticationException)

Admin user not found → 500 вместо 404

prod Flyway config не в spring.flyway + ddl-auto=validate → легко получить падение на старте

logging pattern в application.yml лежит не туда

AuthService.java
GlobalExceptionHandler.java
AdminUserService.java
User.java
db/migration/*.sql
SecurityConfig.java
AuthController.java

 */

@EnableCaching
@SpringBootApplication
public class AuthApplication {

	public static void main(String[] args) {

		SpringApplication.run(AuthApplication.class, args);
	}

}
