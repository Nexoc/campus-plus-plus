package at.campus.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * VerifyResponse
 *
 * Returned when token is valid.
 * Used for frontend session checks.
 */
@Schema(description = "Response returned when JWT token is valid")
public class VerifyResponse {

    @Schema(
            description = "Authenticated user email address",
            example = "user@example.com"
    )
    private final String email;

    @Schema(
            description = "User role",
            example = "STUDENT"
    )
    private final String role;

    public VerifyResponse(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
