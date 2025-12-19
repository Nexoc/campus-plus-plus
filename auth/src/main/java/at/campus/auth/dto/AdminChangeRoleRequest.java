package at.campus.auth.dto;

import at.campus.auth.model.UserRole;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Admin request to change user role")
public class AdminChangeRoleRequest {

    @Schema(
            description = "User ID",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    @NotNull
    private UUID userId;

    @Schema(
            description = "New role for the user",
            example = "ADMIN"
    )
    @NotNull
    private UserRole role;

    public UUID getUserId() {
        return userId;
    }

    public UserRole getRole() {
        return role;
    }
}
