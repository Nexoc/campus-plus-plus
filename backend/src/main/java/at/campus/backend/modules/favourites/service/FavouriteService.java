package at.campus.backend.modules.favourites.service;

import at.campus.backend.common.exception.ForbiddenException;
import at.campus.backend.common.exception.NotFoundException;
import at.campus.backend.modules.favourites.model.Favourite;
import at.campus.backend.modules.favourites.model.StudyProgramFavourite;
import at.campus.backend.modules.favourites.repository.FavouriteRepository;
import at.campus.backend.modules.courses.repository.CourseRepository;
import at.campus.backend.modules.studyprograms.repository.StudyProgramRepository;
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
    private final StudyProgramRepository studyProgramRepository;
    private final UserContext userContext;

    public FavouriteService(
            FavouriteRepository favouriteRepository,
            CourseRepository courseRepository,
            StudyProgramRepository studyProgramRepository,
            UserContext userContext
    ) {
        this.favouriteRepository = favouriteRepository;
        this.courseRepository = courseRepository;
        this.studyProgramRepository = studyProgramRepository;
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
    // STUDY PROGRAM FAVOURITES
    // ==================================================

    /**
     * Get all study program favourites for the authenticated user.
     *
     * @return list of study program favourites
     */
    public List<StudyProgramFavourite> getAllStudyProgramFavourites() {
        requireAuthenticated();

        UUID userId = UUID.fromString(userContext.getUserId());
        return favouriteRepository.findAllStudyProgramsByUserId(userId);
    }

    /**
     * Add a study program to the authenticated user's favourites.
     *
     * @param studyProgramId the study program ID
     * @throws ForbiddenException if user is not authenticated
     * @throws NotFoundException if study program doesn't exist
     * @throws IllegalStateException if already in favourites
     */
    public void addStudyProgramFavourite(UUID studyProgramId) {
        requireAuthenticated();

        UUID userId = UUID.fromString(userContext.getUserId());

        // Validate study program exists
        if (studyProgramRepository.findById(studyProgramId).isEmpty()) {
            log.warn("Attempted to favourite non-existent study program: {}", studyProgramId);
            throw new NotFoundException("Study program not found: " + studyProgramId);
        }

        // Check for duplicates
        if (favouriteRepository.existsByUserIdAndStudyProgramId(userId, studyProgramId)) {
            log.warn("Study program {} is already in favourites for user {}", studyProgramId, userId);
            throw new IllegalStateException("Study program is already in favourites");
        }

        favouriteRepository.insertStudyProgram(userId, studyProgramId);
        log.info("Added study program {} to favourites for user {}", studyProgramId, userId);
    }

    /**
     * Remove a study program from the authenticated user's favourites.
     *
     * @param studyProgramId the study program ID
     * @throws ForbiddenException if user is not authenticated
     */
    public void removeStudyProgramFavourite(UUID studyProgramId) {
        requireAuthenticated();

        UUID userId = UUID.fromString(userContext.getUserId());
        boolean deleted = favouriteRepository.deleteByUserIdAndStudyProgramId(userId, studyProgramId);

        if (deleted) {
            log.info(
                    "Removed study program {} from favourites for user {}",
                    studyProgramId,
                    userId
            );
        } else {
            log.debug(
                    "Study program {} was not in favourites for user {}",
                    studyProgramId,
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
