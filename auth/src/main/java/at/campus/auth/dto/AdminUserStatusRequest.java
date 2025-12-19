package at.campus.auth.dto;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Admin request to enable or disable a user")
public class AdminUserStatusRequest {

    @Schema(
            description = "User ID",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    @NotNull
    private UUID userId;

    public UUID getUserId() {
        return userId;
    }
}
