package at.campus.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    private static final Logger log = LoggerFactory.getLogger(PasswordConfig.class);

    /**
     * BCrypt password encoder.
     *
     * Strength 12 provides a good balance between security
     * and performance for production environments.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        int strength = 12;

        // Log encoder initialization without exposing sensitive data
        log.info("Initializing BCryptPasswordEncoder with strength={}", strength);

        return new BCryptPasswordEncoder(strength);
    }
}
