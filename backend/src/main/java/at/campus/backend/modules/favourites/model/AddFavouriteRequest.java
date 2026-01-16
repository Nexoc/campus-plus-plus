package at.campus.backend.modules.favourites.model;

import java.util.UUID;

/**
 * Request DTO for adding a course to favourites.
 *
 * Responsibilities:
 * - Represents the request body for POST /api/favourites
 * - Contains only the course ID to be added
 *
 * IMPORTANT:
 * - user_id is NOT included (derived from UserContext)
 * - DTO contains NO business logic
 */
public record AddFavouriteRequest(
        UUID courseId
) {
}
