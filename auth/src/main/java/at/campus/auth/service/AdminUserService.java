package at.campus.auth.service;

import at.campus.auth.model.User;
import at.campus.auth.model.UserRole;
import at.campus.auth.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * AdminUserService
 * Admin operations on users:
 * - change role
 * - disable/enable (ban/unban)
 * Each operation bumps token version to revoke existing JWT tokens.
 */
@Service
public class AdminUserService {

    private static final Logger log =
            LoggerFactory.getLogger(AdminUserService.class);

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @CacheEvict(cacheNames = "userDetails", allEntries = true)
    public void changeRole(UUID userId, UserRole newRole) {

        User user = getUser(userId);
        user.changeRole(newRole);

        log.info("User role changed, userId={}, email={}, newRole={}",
                userId, user.getUsername(), newRole);
    }

    @Transactional
    @CacheEvict(cacheNames = "userDetails", allEntries = true)
    public void disableUser(UUID userId) {

        User user = getUser(userId);
        user.disable();

        log.info("User disabled, userId={}, email={}",
                userId, user.getUsername());
    }

    @Transactional
    @CacheEvict(cacheNames = "userDetails", allEntries = true)
    public void enableUser(UUID userId) {

        User user = getUser(userId);
        user.enable();

        log.info("User enabled, userId={}, email={}",
                userId, user.getUsername());
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
