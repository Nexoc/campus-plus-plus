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
 * Business logic for authentication and account security.
 *
 * Design principles:
 * --------------------------------------------------
 * - Stateless authentication (JWT)
 * - No HTTP/session awareness
 * - No direct JWT parsing here
 * - Database is the source of truth
 * - Token revocation via tokenVersion
 *
 * Responsibilities:
 * --------------------------------------------------
 * - Register new users
 * - Authenticate credentials
 * - Issue JWT tokens
 * - Change passwords
 * - Provide access to current authenticated user
 */
@Service
public class AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthService.class);

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

    /**
     * Registers a new user account.
     *
     * Security notes:
     * - Default role is assigned server-side
     * - Password is hashed immediately
     * - Email uniqueness is enforced
     */
    public void register(String email, String rawPassword, String nickname) {

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

        // Fallback nickname if none provided
        if (nickname == null || nickname.isBlank()) {
            nickname = email.split("@")[0];
        }

        user.setNickname(nickname);

        userRepository.save(user);

        log.info(
                "User successfully registered, email={}, nickname={}, role={}",
                email, nickname, UserRole.STUDENT
        );
    }

    /**
     * Authenticates user credentials and issues a JWT token.
     *
     * Security notes:
     * - Authentication is delegated to AuthenticationManager
     * - No SecurityContext manipulation here
     * - JWT is issued only after successful authentication
     */
    public AuthResponse login(String email, String password) {

        log.info("Login attempt for email={}", email);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (Exception ex) {
            // ВАЖНО: маппим ВСЁ в доменное исключение
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    /**
     * Changes password for the currently authenticated user.
     *
     * Effects:
     * - Verifies current password
     * - Stores new password hash
     * - Increments tokenVersion (revokes existing JWTs)
     * - Evicts cached UserDetails
     */
    @CacheEvict(
            cacheNames = "userDetails",
            key = "#root.target.getCurrentUserEmail()"
    )
    public void changePassword(String currentPassword, String newPassword) {

        String email = getCurrentUserEmail();
        log.info("Password change attempt for email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("Password change failed: invalid current password, email={}", email);
            throw new InvalidCredentialsException();
        }

        String newHash = passwordEncoder.encode(newPassword);
        user.changePasswordHash(newHash);

        userRepository.save(user);

        log.info("Password successfully changed for email={}", email);
    }

    /**
     * Returns email of the currently authenticated user.
     *
     * Used for:
     * - cache eviction
     * - internal service logic
     */
    public String getCurrentUserEmail() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    /**
     * Loads the currently authenticated user entity.
     */
    public User getCurrentUser() {
        String email = getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
    }
}
