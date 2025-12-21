package at.campus.auth.dto;

import at.campus.auth.model.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.UUID;

/**
 * AdminUserResponse
 *
 * DTO returned by admin endpoints for user listing.
 *
 * Design goals:
 * - Immutable API contract (getters only)
 * - ISO-8601 date format for frontend compatibility
 * - No domain logic
 */
public class AdminUserResponse {

    private final UUID id;
    private final String email;
    private final String nickname;
    private final UserRole role;
    private final boolean enabled;
    private final boolean accountNonLocked;

    /**
     * User creation timestamp.
     *
     * Serialized as ISO-8601 string, e.g.:
     * "2025-01-21T15:42:17.123Z"
     */
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            timezone = "UTC"
    )
    private final Instant createdAt;

    public AdminUserResponse(
            UUID id,
            String email,
            String nickname,
            UserRole role,
            boolean enabled,
            boolean accountNonLocked,
            Instant createdAt
    ) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.createdAt = createdAt;
    }

    // -------------------------
    // Getters (read-only DTO)
    // -------------------------

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
