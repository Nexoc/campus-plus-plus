package at.campus.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User registration request")
public class RegisterRequest {

    @Schema(
            description = "User email address",
            example = "newuser@example.com"
    )
    @Email
    @NotBlank
    private String email;

    @Schema(
            description = "User password (minimum 8 characters)",
            example = "StrongPassword123!"
    )
    @NotBlank
    @Size(min = 8)
    private String password;

    @Schema(
            description = "Public user nickname (optional)",
            example = "johnny"
    )
    private String nickname; // 👈 NEW

    // --------------------------------------------------
    // Getters
    // --------------------------------------------------

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
}
