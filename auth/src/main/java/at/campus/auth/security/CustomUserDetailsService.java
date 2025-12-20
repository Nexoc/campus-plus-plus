package at.campus.auth.security;

import at.campus.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



/**
 * CustomUserDetailsService
 * Loads user data for Spring Security authentication.
 * Cached to reduce database hits for repeated JWT requests.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log =
            LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user by email (used as username).
     * Results are cached for a short TTL (configured in application.yml).
     */
    @Override
    @Cacheable(cacheNames = "userDetails", key = "#email")
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        log.debug("Loading user by email={}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found for email={}", email);
                    return new UsernameNotFoundException("User not found: " + email);
                });
    }
}
