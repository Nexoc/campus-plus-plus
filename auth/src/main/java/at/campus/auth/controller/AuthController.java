package at.campus.auth.controller;

import at.campus.auth.dto.LoginRequest;
import at.campus.auth.dto.RegisterRequest;
import at.campus.auth.dto.AuthResponse;
import at.campus.auth.model.User;
import at.campus.auth.service.AuthService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * AuthController
 * REST controller responsible for authentication endpoints.
 * Endpoints:
 * - POST /auth/register
 * - POST /auth/login
 * - GET  /auth/validate   (internal, for nginx auth_request)
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /* =========================
       Register
       ========================= */

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with email and password"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "409", description = "Email already exists"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping(
            value = "/register",
            consumes = "application/json"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public void register(
            @Valid @RequestBody RegisterRequest request
    ) {
        log.info("Received registration request for email={}", request.getEmail());

        authService.register(
                request.getEmail(),
                request.getPassword()
        );

        log.info("Registration request successfully processed for email={}", request.getEmail());
    }

    /* =========================
       Login
       ========================= */

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates user credentials and returns a JWT token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping(
            value = "/login",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        log.info("Received login request for email={}", request.getEmail());

        AuthResponse response = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        log.info("Login request successfully processed for email={}", request.getEmail());

        return ResponseEntity.ok(response);
    }

    /* =========================
       Validate (for NGINX)
       ========================= */

    @Operation(
            summary = "Validate JWT token (internal)",
            description = "Used by NGINX auth_request to validate JWT and extract user info"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "401", description = "Token is invalid or expired")
    })
    @GetMapping("/validate")
    public ResponseEntity<Void> validate(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok()
                .header("X-User-Id", user.getId().toString())
                .header("X-User-Roles", user.getRole().name())
                .build();
    }

    /**
     * CSRF initialization endpoint.
     *
     * Purpose:
     * - Forces Spring Security to generate CSRF token
     * - CSRF token is automatically written into cookie (XSRF-TOKEN)
     *
     * Frontend MUST call this endpoint before:
     * - POST /auth/login
     * - POST /auth/register
     */
    @GetMapping("/csrf")
    public void csrf() {
        // Intentionally empty.
        // Spring Security handles CSRF token creation automatically.
    }
}
