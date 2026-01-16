package at.campus.auth.service;

import at.campus.auth.dto.AdminUserResponse;
import at.campus.auth.model.User;
import at.campus.auth.model.UserRole;
import at.campus.auth.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * AdminUserService
 *
 * Admin operations on users:
 * - change role
 * - disable / enable (ban / unban)
 *
 * Each mutation bumps token version
 * to invalidate existing JWTs.
 */
@Service
public class AdminUserService {

    private static final Logger log =
            LoggerFactory.getLogger(AdminUserService.class);

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // --------------------------------------------------
    // Mutations
    // --------------------------------------------------

    @Transactional
    @CacheEvict(cacheNames = "userDetails", allEntries = true)
    public void changeRole(UUID userId, UserRole newRole) {

        User user = getUser(userId);
        user.changeRole(newRole);

        log.info(
                "User role changed, userId={}, email={}, newRole={}",
                userId,
                user.getEmail(),
                newRole
        );
    }

    @Transactional
    @CacheEvict(cacheNames = "userDetails", allEntries = true)
    public void disableUser(UUID userId) {

        User user = getUser(userId);
        user.disable();

        log.info(
                "User disabled, userId={}, email={}",
                userId,
                user.getEmail()
        );
    }

    @Transactional
    @CacheEvict(cacheNames = "userDetails", allEntries = true)
    public void enableUser(UUID userId) {

        User user = getUser(userId);
        user.enable();

        log.info(
                "User enabled, userId={}, email={}",
                userId,
                user.getEmail()
        );
    }

    // --------------------------------------------------
    // Queries
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<AdminUserResponse> listUsers() {

        log.info("Listing all users (Moderator)");

        return userRepository.findAll().stream()
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getNickname(),
                        user.getRole(),
                        user.isEnabled(),
                        user.isAccountNonLocked(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    // --------------------------------------------------
    // Helpers
    // --------------------------------------------------

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }
}
