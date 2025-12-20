package at.campus.auth.controller;

import at.campus.auth.dto.ChangePasswordRequest;
import at.campus.auth.service.AuthService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * AccountController
 * Endpoints for authenticated users.
 */
@RestController
@RequestMapping("/account")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AuthService authService;

    public AccountController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Changes password for the currently authenticated user.
     */
    @Operation(
            summary = "Change password",
            description = "Changes password for the currently authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password successfully changed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping(
            value = "/change-password",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        log.info("Received change-password request");

        authService.changePassword(
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        log.info("Change-password request successfully processed");

        return ResponseEntity.noContent().build();
    }
}
