package at.campus.auth.service;

import at.campus.auth.dto.AuthResponse;
import at.campus.auth.exception.EmailAlreadyExistsException;
import at.campus.auth.exception.InvalidCredentialsException;
import at.campus.auth.model.User;
import at.campus.auth.model.UserRole;
import at.campus.auth.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService
 *
 * Authentication-related business logic.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String email, String rawPassword) {

        log.info("Registration attempt for email={}", email);

        if (userRepository.existsByEmail(email)) {
            log.warn("Registration failed: email already exists, email={}", email);
            throw new EmailAlreadyExistsException(email);
        }

        String passwordHash = passwordEncoder.encode(rawPassword);

        User user = new User(
                email,
                passwordHash,
                UserRole.STUDENT
        );

        userRepository.save(user);

        log.info("User successfully registered, email={}, role={}", email, UserRole.STUDENT);
    }

    public AuthResponse login(String email, String rawPassword) {

        log.info("Login attempt for email={}", email);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, rawPassword)
            );
        } catch (AuthenticationException ex) {
            log.warn("Login failed: invalid credentials, email={}", email);
            throw new InvalidCredentialsException();
        }

        log.debug("AuthenticationManager successfully authenticated email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Authenticated user not found in database, email={}", email);
                    return new InvalidCredentialsException();
                });

        String token = jwtService.generateToken(user);

        log.info("JWT token successfully generated for email={}", email);

        return new AuthResponse(token);
    }

    /**
     * Changes password for the currently authenticated user.
     * - verifies current password
     * - stores new password hash
     * - bumps token version to revoke old JWTs
     */
    @CacheEvict(cacheNames = "userDetails", key = "#root.target.getCurrentUserEmail()")
    public void changePassword(String currentPassword, String newPassword) {

        String email = getCurrentUserEmail();
        log.info("Password change attempt for email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        // Verify current password against stored hash
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("Password change failed: invalid current password, email={}", email);
            throw new InvalidCredentialsException();
        }

        // Save new hash and revoke old tokens
        String newHash = passwordEncoder.encode(newPassword);
        user.changePasswordHash(newHash);

        userRepository.save(user);

        log.info("Password successfully changed for email={}", email);
    }

    /**
     * Helper for SpEL in @CacheEvict and for internal use.
     */
    public String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
