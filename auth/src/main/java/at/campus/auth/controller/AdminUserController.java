package at.campus.auth.controller;

import at.campus.auth.dto.AdminChangeRoleRequest;
import at.campus.auth.dto.AdminUserResponse;
import at.campus.auth.dto.AdminUserStatusRequest;
import at.campus.auth.service.AdminUserService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

/**
 * AdminUserController
 * Admin endpoints for user management (requires ROLE_ADMIN).
 */


@RestController
@RequestMapping("/admin/users")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @Operation(
            summary = "Change user role",
            description = "Changes the role of a user. Requires ROLE_ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Role successfully changed"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema()))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(
            value = "/change-role",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<Void> changeRole(
            @Valid @RequestBody AdminChangeRoleRequest request
    ) {
        log.info("Admin change-role request for userId={}, role={}",
                request.getUserId(), request.getRole());

        adminUserService.changeRole(request.getUserId(), request.getRole());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Disable user",
            description = "Disables (bans) a user account. Requires ROLE_ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User successfully disabled"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema()))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(
            value = "/disable",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<Void> disable(
            @Valid @RequestBody AdminUserStatusRequest request
    ) {
        log.info("Admin disable request for userId={}", request.getUserId());

        adminUserService.disableUser(request.getUserId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Enable user",
            description = "Enables (unbans) a user account. Requires ROLE_ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User successfully enabled"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema()))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(
            value = "/enable",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<Void> enable(
            @Valid @RequestBody AdminUserStatusRequest request
    ) {
        log.info("Admin enable request for userId={}", request.getUserId());

        adminUserService.enableUser(request.getUserId());
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "List all users",
            description = "Returns a list of all users. Requires ROLE_ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users list returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<AdminUserResponse>> listUsers() {
        log.info("Admin list users request");

        return ResponseEntity.ok(adminUserService.listUsers());
    }

}
