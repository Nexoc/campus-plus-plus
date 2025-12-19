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

/**
 * JwtAuthenticationFilter
 *
 * JWT authentication filter:
 * - validates signature + expiration
 * - loads user to enforce enabled/locked
 * - checks token version for revocation
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        // 1. Validate token (signature + expiration)
        if (!jwtService.isTokenValid(jwt)) {
            log.warn("Invalid JWT token for request {} {}", request.getMethod(), request.getRequestURI());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        // 2. Extract username
        String username;
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception ex) {
            log.warn("Failed to extract JWT subject");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        // 3. Load user
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (Exception ex) {
            log.warn("User not found for JWT subject, username={}", username);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        // 4. Check account status
        if (!userDetails.isEnabled() || !userDetails.isAccountNonLocked()) {
            log.warn("User is disabled or locked, username={}", username);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User account disabled");
            return;
        }

        // 5. Token version check (revocation)
        int tokenVer = jwtService.extractTokenVersion(jwt);

        if (!(userDetails instanceof User user)) {
            log.error("UserDetails is not instance of User, cannot check tokenVersion");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        if (tokenVer != user.getTokenVersion()) {
            log.warn(
                    "JWT revoked by version mismatch, username={}, tokenVer={}, userVer={}",
                    username, tokenVer, user.getTokenVersion()
            );
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token revoked");
            return;
        }

        // 6. Authenticate
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
