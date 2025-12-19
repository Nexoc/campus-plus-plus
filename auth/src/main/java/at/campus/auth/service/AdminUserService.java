package at.campus.auth.service;

import at.campus.auth.model.User;
import at.campus.auth.model.UserRole;
import at.campus.auth.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * AdminUserService
 *
 * Admin operations on users:
 * - change role
 * - disable/enable (ban/unban)
 *
 * Each operation bumps token version to revoke existing JWT tokens.
 */
@Service
public class AdminUserService {

    private static final Logger log = LoggerFactory.getLogger(AdminUserService.class);

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @CacheEvict(cacheNames = "userDetails", allEntries = true)
    public void changeRole(UUID userId, UserRole newRole) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.changeRole(newRole); // bumps tokenVersion inside
        userRepository.save(user);

        log.info("User role changed, userId={}, email={}, newRole={}",
                userId, user.getUsername(), newRole);
    }

    @CacheEvict(cacheNames = "userDetails", allEntries = true)
    public void disableUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.disable(); // sets enabled=false + bumps tokenVersion
        userRepository.save(user);

        log.info("User disabled, userId={}, email={}", userId, user.getUsername());
    }

    @CacheEvict(cacheNames = "userDetails", allEntries = true)
    public void enableUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.enable(); // sets enabled=true + bumps tokenVersion
        userRepository.save(user);

        log.info("User enabled, userId={}, email={}", userId, user.getUsername());
    }
}
