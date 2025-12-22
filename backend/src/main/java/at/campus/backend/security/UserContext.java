package at.campus.backend.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Set;

/**
 * UserContext represents the identity of the currently authenticated user.
 *
 * PURPOSE
 * --------------------------------------------------
 * This class provides access to the identity of the
 * currently authenticated user inside the backend.
 *
 * IMPORTANT ARCHITECTURAL RULES
 * --------------------------------------------------
 * - This class is NOT related to Spring Security.
 * - This backend does NOT parse or validate JWT tokens.
 * - Authentication is handled entirely by the gateway (Nginx).
 * - Identity is trusted and propagated via HTTP headers.
 *
 * IDENTITY SOURCE
 * --------------------------------------------------
 * The gateway injects the following headers:
 *
 *   X-User-Id     -> unique user identifier
 *   X-User-Roles  -> comma-separated list of roles
 *
 * LIFECYCLE
 * --------------------------------------------------
 * - One instance per HTTP request
 * - Automatically created by Spring
 * - Destroyed after request completion
 *
 * USAGE
 * --------------------------------------------------
 * - Injected into services
 * - Used ONLY for business decisions
 * - Controllers must stay thin
 */
@Component
@RequestScope
public class UserContext {

    /**
     * Unique identifier of the authenticated user.
     * Provided by the Auth Service via the gateway.
     */
    private String userId;

    /**
     * Set of roles assigned to the user.
     * Example: STUDENT, MODERATOR, ADMIN
     */
    private Set<String> roles;

    // Setters are used by UserContextFilter
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    // Read-only access for application code
    public String getUserId() {
        return userId;
    }

    public Set<String> getRoles() {
        return roles;
    }

    /**
     * Convenience helper for role checks.
     *
     * NOTE:
     * - Business logic MUST stay in services.
     * - Controllers must NOT perform authorization logic.
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}
