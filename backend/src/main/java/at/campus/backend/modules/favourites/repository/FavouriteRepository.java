package at.campus.backend.modules.favourites.repository;

import at.campus.backend.modules.favourites.model.Favourite;

import java.util.List;
import java.util.UUID;

/**
 * Repository abstraction for Favourite persistence.
 *
 * SQL-first design.
 * No JPA.
 */
public interface FavouriteRepository {

    // ==================================================
    // READ
    // ==================================================

    /**
     * Find all favourites for a specific user.
     * Results are enriched with course details.
     *
     * @param userId the user ID
     * @return list of favourites (empty if none)
     */
    List<Favourite> findAllByUserId(UUID userId);

    /**
     * Check if a course is already in the user's favourites.
     *
     * @param userId the user ID
     * @param courseId the course ID
     * @return true if favourite exists, false otherwise
     */
    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);

    // ==================================================
    // WRITE
    // ==================================================

    /**
     * Add a course to the user's favourites.
     *
     * @param userId the user ID
     * @param courseId the course ID
     */
    void insert(UUID userId, UUID courseId);

    /**
     * Remove a course from the user's favourites.
     *
     * @param userId the user ID
     * @param courseId the course ID
     * @return true if deleted, false if not found
     */
    boolean deleteByUserIdAndCourseId(UUID userId, UUID courseId);
}
