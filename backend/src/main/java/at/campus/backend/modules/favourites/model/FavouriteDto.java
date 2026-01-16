package at.campus.backend.modules.favourites.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO exposed via REST API for favourites.
 *
 * Responsibilities:
 * - Represents Favourite data in HTTP responses
 * - Converts between API representation and domain model
 *
 * IMPORTANT:
 * - DTO contains NO business logic
 * - DTO does NOT access database
 */
public record FavouriteDto(
        UUID courseId,
        String courseTitle,
        String courseDescription,
        Integer courseEcts,
        LocalDateTime createdAt
) {

    // -------------------------
    // DOMAIN -> DTO
    // -------------------------
    public static FavouriteDto fromDomain(Favourite favourite) {
        return new FavouriteDto(
                favourite.getCourseId(),
                favourite.getCourseTitle(),
                favourite.getCourseDescription(),
                favourite.getCourseEcts(),
                favourite.getCreatedAt()
        );
    }
}
