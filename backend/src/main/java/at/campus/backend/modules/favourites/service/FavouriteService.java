package at.campus.backend.modules.favourites.service;

import at.campus.backend.common.exception.ForbiddenException;
import at.campus.backend.common.exception.NotFoundException;
import at.campus.backend.modules.favourites.model.Favourite;
import at.campus.backend.modules.favourites.repository.FavouriteRepository;
import at.campus.backend.modules.courses.repository.CourseRepository;
import at.campus.backend.security.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for managing user favourites.
 *
 * Responsibilities:
 * - Business logic for favourites management
 * - Access control (authenticated users only)
 * - Validation (course exists, no duplicates)
 */
@Service
public class FavouriteService {

    private static final Logger log =
            LoggerFactory.getLogger(FavouriteService.class);

    private final FavouriteRepository favouriteRepository;
    private final CourseRepository courseRepository;
    private final UserContext userContext;

    public FavouriteService(
            FavouriteRepository favouriteRepository,
            CourseRepository courseRepository,
            UserContext userContext
    ) {
        this.favouriteRepository = favouriteRepository;
        this.courseRepository = courseRepository;
        this.userContext = userContext;
    }

    // ==================================================
    // READ OPERATIONS
    // ==================================================

    /**
     * Get all favourites for the authenticated user.
     *
     * @return list of favourites (empty if none)
     */
    public List<Favourite> getUserFavourites() {
        requireAuthenticated();

        UUID userId = UUID.fromString(userContext.getUserId());

        log.debug(
                "Fetching favourites for user {}",
                userId
        );

        return favouriteRepository.findAllByUserId(userId);
    }

    // ==================================================
    // WRITE OPERATIONS
    // ==================================================

    /**
     * Add a course to the authenticated user's favourites.
     *
     * @param courseId the course ID to add
     * @throws NotFoundException if course does not exist
     * @throws IllegalStateException if course is already in favourites
     */
    public void addFavourite(UUID courseId) {
        requireAuthenticated();

        UUID userId = UUID.fromString(userContext.getUserId());

        log.debug(
                "User {} adding course {} to favourites",
                userId,
                courseId
        );

        // Verify course exists
        if (courseRepository.findById(courseId).isEmpty()) {
            log.warn(
                    "User {} tried to favourite non-existent course {}",
                    userId,
                    courseId
            );
            throw new NotFoundException("Course not found: " + courseId);
        }

        // Check if already in favourites
        if (favouriteRepository.existsByUserIdAndCourseId(userId, courseId)) {
            log.debug(
                    "Course {} already in favourites for user {}",
                    courseId,
                    userId
            );
            throw new IllegalStateException("Course is already in favourites");
        }

        // Add to favourites
        try {
            favouriteRepository.insert(userId, courseId);

            log.info(
                    "User {} added course {} to favourites",
                    userId,
                    courseId
            );
        } catch (DuplicateKeyException e) {
            // Race condition - course was added between check and insert
            log.warn(
                    "Duplicate favourite detected for user {} and course {}",
                    userId,
                    courseId
            );
            throw new IllegalStateException("Course is already in favourites");
        }
    }

    /**
     * Remove a course from the authenticated user's favourites.
     *
     * @param courseId the course ID to remove
     */
    public void removeFavourite(UUID courseId) {
        requireAuthenticated();

        UUID userId = UUID.fromString(userContext.getUserId());

        log.debug(
                "User {} removing course {} from favourites",
                userId,
                courseId
        );

        boolean deleted = favouriteRepository.deleteByUserIdAndCourseId(userId, courseId);

        if (deleted) {
            log.info(
                    "User {} removed course {} from favourites",
                    userId,
                    courseId
            );
        } else {
            log.debug(
                    "Course {} was not in favourites for user {}",
                    courseId,
                    userId
            );
        }

        // Graceful handling - removing non-existent favourite is not an error
    }

    // ==================================================
    // ACCESS POLICY
    // ==================================================

    /**
     * Ensure user is authenticated.
     * Unauthenticated users (applicants) are forbidden.
     */
    private void requireAuthenticated() {
        if (userContext.getUserId() == null || userContext.getUserId().isBlank()) {
            log.warn("Unauthenticated user tried to access favourites");
            throw new ForbiddenException("Authentication required");
        }
    }
}
