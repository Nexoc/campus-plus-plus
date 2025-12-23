package at.campus.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * UserContextFilter
 * ==================================================
 *
 * Servlet filter responsible for populating UserContext
 * from HTTP headers injected by the gateway (Nginx).
 *
 * SECURITY MODEL
 * --------------------------------------------------
 * - Authentication is performed by Nginx via auth_request.
 * - Backend TRUSTS the gateway completely.
 * - Backend NEVER parses or validates JWT tokens.
 *
 * EXPECTED HEADERS
 * --------------------------------------------------
 *   X-User-Id     -> unique user identifier
 *   X-User-Roles  -> single role (one role per user)
 *
 * LIFECYCLE
 * --------------------------------------------------
 * - Runs once per HTTP request
 * - Executed BEFORE controllers and services
 * - Populates request-scoped UserContext
 * - Does NOT perform authorization
 */
@Component
public class UserContextFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLES_HEADER = "X-User-Roles";

    private final UserContext userContext;

    /**
     * UserContext is injected by Spring.
     *
     * Because it is @RequestScope, this instance
     * is unique per HTTP request.
     */
    public UserContextFilter(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // --------------------------------------------------
        // Extract identity headers injected by the gateway
        // --------------------------------------------------
        String userId = request.getHeader(USER_ID_HEADER);
        String roleHeader = request.getHeader(USER_ROLES_HEADER);

        // --------------------------------------------------
        // Populate UserContext ONLY if userId is present
        // --------------------------------------------------
        // If X-User-Id is missing, the request is considered
        // unauthenticated and UserContext remains empty.
        if (userId != null) {

            userContext.setUserId(userId);

            // --------------------------------------------------
            // Populate role ONLY if header is present and valid
            // --------------------------------------------------
            // System guarantees exactly ONE role per user.
            if (roleHeader != null) {
                String role = roleHeader.trim();

                // Ignore empty or blank roles
                if (!role.isBlank()) {
                    userContext.setRoles(Set.of(role));
                }
            }
        }

        // --------------------------------------------------
        // Continue filter chain
        // --------------------------------------------------
        // No cleanup required:
        // UserContext is request-scoped and will be
        // destroyed automatically after request completion.
        filterChain.doFilter(request, response);
    }
}
