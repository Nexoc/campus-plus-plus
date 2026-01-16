package at.campus.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * UserRole
 *
 * Defines roles available in the system.
 */
@Schema(
        description = "Role assigned to a user",
        example = "STUDENT"
)
public enum UserRole {

    @Schema(description = "Unauthenticated or limited-access user")
    Applicant,

    @Schema(description = "Standard authenticated user")
    STUDENT,

    @Schema(description = "Moderator with elevated privileges")
    Moderator
}
