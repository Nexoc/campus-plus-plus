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
    @Email(message = "Ungueltige E-Mail-Adresse.")
    @NotBlank(message = "E-Mail ist erforderlich.")
    private String email;

    @Schema(
            description = "User password (minimum 8 characters)",
            example = "StrongPassword123!"
    )
    @NotBlank(message = "Passwort ist erforderlich.")
    @Size(min = 8, message = "Passwort muss mindestens 8 Zeichen lang sein.")
    private String password;

    @Schema(
            description = "Public user nickname",
            example = "johnny"
    )
    @NotBlank(message = "Nickname ist erforderlich.")
    @Size(min = 3, max = 50, message = "Nickname muss zwischen 3 und 50 Zeichen lang sein.")
    private String nickname;

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
