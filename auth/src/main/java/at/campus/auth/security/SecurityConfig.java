package at.campus.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

/**
 * SecurityConfig
 *
 * Central Spring Security configuration.
 *
 * Design goals:
 * --------------------------------------------------
 * - Stateless authentication using JWT
 * - NO HTTP session
 * - JWT is always sent via Authorization header
 * - Cookies are opt-in (only when explicitly enabled)
 * - CSRF protection is ENABLED for sensitive browser endpoints
 * - CSRF is DISABLED for login / register
 * - Method-level security (@PreAuthorize) is enabled
 *
 * Security model:
 * --------------------------------------------------
 * Authentication:
 *   - JWT (Authorization: Bearer <token>)
 *
 * CSRF:
 *   - Enabled globally
 *   - Uses cookie-based CSRF token (XSRF-TOKEN)
 *   - Required ONLY for state-changing browser endpoints
 *   - Explicitly DISABLED for /auth/login and /auth/register
 *
 * Sessions:
 *   - Completely disabled (STATELESS)
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    /**
     * Main SecurityFilterChain configuration.
     *
     * IMPORTANT:
     * This application is fully stateless.
     * No HTTP session is ever created or used.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // --------------------------------------------------
                // CSRF CONFIGURATION
                // --------------------------------------------------
                // CSRF is ENABLED globally, but explicitly DISABLED
                // for login and register endpoints.
                //
                // Why?
                // - Login/Register do not rely on cookies for auth
                // - They are protected by credentials (email/password)
                // - Requiring CSRF for login would complicate UX
                //
                // CSRF IS required for:
                // - account endpoints
                // - admin endpoints
                // --------------------------------------------------
                .csrf(csrf -> csrf
                        // Store CSRF token in a cookie so that
                        // browser-based frontend can read it
                        // and send it back via X-XSRF-TOKEN header.
                        //
                        // HttpOnly MUST be false, otherwise
                        // JavaScript cannot read the token.
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository.withHttpOnlyFalse()
                        )

                        // Default request handler for CSRF tokens.
                        // This ensures the token is exposed as request attribute
                        // and properly validated.
                        .csrfTokenRequestHandler(
                                new CsrfTokenRequestAttributeHandler()
                        )

                        // Disable CSRF protection for specific endpoints.
                        // These endpoints MUST NOT require CSRF tokens.
                        .ignoringRequestMatchers(
                                "/auth/login",
                                "/auth/register",
                                "/auth/validate"

                        )
                )

                // --------------------------------------------------
                // CORS CONFIGURATION
                // --------------------------------------------------
                // CORS is configured elsewhere (or default).
                // This call explicitly enables CORS support.
                .cors(cors -> {})

                // --------------------------------------------------
                // SESSION MANAGEMENT
                // --------------------------------------------------
                // Enforce stateless behavior.
                // Spring Security MUST NOT create or use HTTP sessions.
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // --------------------------------------------------
                // AUTHORIZATION RULES
                // --------------------------------------------------
                .authorizeHttpRequests(auth -> auth

                        // Public endpoints (no authentication required)
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register",
                                //"/auth/csrf",
                                "/auth/validate",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/health"
                        ).permitAll()

                        // Admin endpoints
                        // Role-based access control
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Any other endpoint requires authentication
                        .anyRequest().authenticated()
                )

                // --------------------------------------------------
                // EXCEPTION HANDLING
                // --------------------------------------------------
                // Centralized handling of:
                // - Authentication failures (401)
                // - Authorization failures (403)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // --------------------------------------------------
                // JWT AUTHENTICATION FILTER
                // --------------------------------------------------
                // Custom filter that:
                // - Extracts JWT from Authorization header
                // - Validates token
                // - Sets SecurityContext if token is valid
                //
                // IMPORTANT:
                // This filter MUST:
                // - Ignore requests WITHOUT Authorization header
                // - NOT throw exceptions if JWT is missing
                // - Simply pass the request down the chain
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Exposes AuthenticationManager bean.
     *
     * Required for:
     * - manual authentication (login endpoint)
     * - authentication providers
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
