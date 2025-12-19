package at.campus.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Change password request for authenticated user")
public class ChangePasswordRequest {

    @Schema(
            description = "Current password",
            example = "OldPassword123!"
    )
    @NotBlank
    private String currentPassword;

    @Schema(
            description = "New password (minimum 8 characters)",
            example = "NewStrongPassword123!"
    )
    @NotBlank
    @Size(min = 8)
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
