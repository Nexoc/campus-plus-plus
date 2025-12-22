package at.campus.auth.security;

import at.campus.auth.model.User;
import at.campus.auth.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JwtAuthenticationFilter
 *
 * Responsibilities:
 * - Intercepts every HTTP request exactly once
 * - Skips JWT processing for public endpoints (login, register, csrf)
 * - Validates JWT signature and expiration
 * - Loads user details from database
 * - Checks account status (enabled / locked)
 * - Verifies token version (revocation support)
 * - Populates Spring SecurityContext if authentication is successful
 *
 * IMPORTANT:
 * - This filter does NOT create sessions (stateless)
 * - This filter does NOT handle CSRF
 * - This filter ONLY handles JWT-based authentication
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /**
     * List of public endpoints that MUST NOT be processed by JWT filter.
     *
     * Reason:
     * - Login/register must work without Authorization header
     * - CSRF bootstrap endpoint must be reachable anonymously
     */
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/csrf"
    );

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Decides whether this filter should be skipped for the current request.
     *
     * If request URI starts with one of PUBLIC_ENDPOINTS,
     * JWT authentication is NOT applied.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }


    /**
     * Core JWT authentication logic.
     *
     * This method runs ONLY if shouldNotFilter() returned false.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.debug(
                "[JWT FILTER] Processing request: {} {}",
                request.getMethod(),
                request.getRequestURI()
        );

        // --------------------------------------------------
        // 1. Read Authorization header
        // --------------------------------------------------
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("[JWT FILTER] No Bearer token found, continuing without authentication");
            filterChain.doFilter(request, response);
            return;
        }

        // --------------------------------------------------
        // 2. Skip if SecurityContext already has authentication
        // --------------------------------------------------
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            log.debug("[JWT FILTER] Authentication already present, skipping");
            filterChain.doFilter(request, response);
            return;
        }

        // --------------------------------------------------
        // 3. Extract raw JWT token
        // --------------------------------------------------
        String jwt = authHeader.substring(7);
        log.debug("[JWT FILTER] JWT extracted");

        // --------------------------------------------------
        // 4. Validate JWT signature & expiration
        // --------------------------------------------------
        if (!jwtService.isTokenValid(jwt)) {
            log.warn("[JWT FILTER] Invalid or expired JWT token");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        // --------------------------------------------------
        // 5. Extract username (subject)
        // --------------------------------------------------
        String username;
        try {
            username = jwtService.extractUsername(jwt);
            log.debug("[JWT FILTER] JWT subject extracted: {}", username);
        } catch (Exception ex) {
            log.warn("[JWT FILTER] Failed to extract username from JWT");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        // --------------------------------------------------
        // 6. Load user from database
        // --------------------------------------------------
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (Exception ex) {
            log.warn("[JWT FILTER] User not found for username={}", username);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        // --------------------------------------------------
        // 7. Check account status
        // --------------------------------------------------
        if (!userDetails.isEnabled() || !userDetails.isAccountNonLocked()) {
            log.warn("[JWT FILTER] User disabled or locked: {}", username);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User account disabled");
            return;
        }

        // --------------------------------------------------
        // 8. Token version check (revocation support)
        // --------------------------------------------------
        if (!(userDetails instanceof User user)) {
            log.error("[JWT FILTER] UserDetails is not instance of User");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        int tokenVersion = jwtService.extractTokenVersion(jwt);

        if (tokenVersion != user.getTokenVersion()) {
            log.warn(
                    "[JWT FILTER] Token revoked: username={}, tokenVer={}, userVer={}",
                    username,
                    tokenVersion,
                    user.getTokenVersion()
            );
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token revoked");
            return;
        }

        // --------------------------------------------------
        // 9. Create Authentication and set SecurityContext
        // --------------------------------------------------
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("[JWT FILTER] Authentication successful for user={}", username);

        // --------------------------------------------------
        // 10. Continue filter chain
        // --------------------------------------------------
        filterChain.doFilter(request, response);
    }
}
