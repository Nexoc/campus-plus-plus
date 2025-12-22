package at.campus.backend.modules.courses.service;

import at.campus.backend.modules.courses.model.Course;
import at.campus.backend.modules.courses.repository.CourseRepository;
import at.campus.backend.security.UserContext;
import at.campus.backend.common.exception.ForbiddenException;
import at.campus.backend.common.exception.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CourseService
 * ==================================================
 *
 * Business service for working with courses.
 *
 * RESPONSIBILITIES
 * --------------------------------------------------
 * - Implements business use-cases for courses
 * - Applies access rules based on UserContext
 * - Delegates persistence to CourseRepository
 *
 * IMPORTANT ARCHITECTURAL RULES
 * --------------------------------------------------
 * - This service does NOT know about JWT
 * - This service does NOT know about HTTP headers
 * - This service trusts UserContext provided by the gateway
 *
 * ACCESS MODEL
 * --------------------------------------------------
 * - READ operations: allowed for all authenticated users
 * - WRITE operations: ADMIN only
 *
 * CONTROLLERS MUST:
 * --------------------------------------------------
 * - NOT perform role checks
 * - NOT contain business logic
 * - Only translate HTTP <-> service calls
 *
 * LOGGING POLICY
 * --------------------------------------------------
 * - INFO  : business actions (create/update/delete)
 * - WARN  : forbidden access, not-found situations
 * - DEBUG : read operations / filters
 */
@Service
public class CourseService {

    private static final Logger log =
            LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository repository;
    private final UserContext userContext;

    public CourseService(
            CourseRepository repository,
            UserContext userContext
    ) {
        this.repository = repository;
        this.userContext = userContext;
    }

    // ==================================================
    // READ OPERATIONS
    // ==================================================

    /**
     * Returns list of courses.
     *
     * Filtering is optional and controlled via query parameters.
     *
     * Access:
     * - Any authenticated user
     */
    public List<Course> getCourses(String studyProgramId, Integer ects) {

        log.debug(
                "Fetching courses (studyProgramId={}, ects={})",
                studyProgramId,
                ects
        );

        if (studyProgramId == null && ects == null) {
            return repository.findAll();
        }

        return repository.findFiltered(studyProgramId, ects);
    }

    /**
     * Returns a single course by ID.
     *
     * Access:
     * - Any authenticated user
     *
     * @throws NotFoundException if course does not exist
     */
    public Course getCourseById(String courseId) {

        log.debug(
                "Fetching course by id={} (user={})",
                courseId,
                userContext.getUserId()
        );

        return repository.findById(courseId)
                .orElseThrow(() -> {
                    log.warn(
                            "Course {} not found (user={})",
                            courseId,
                            userContext.getUserId()
                    );
                    return new NotFoundException(
                            "Course not found: " + courseId
                    );
                });
    }

    // ==================================================
    // WRITE OPERATIONS (ADMIN ONLY)
    // ==================================================

    /**
     * Creates a new course.
     *
     * Access:
     * - ADMIN only
     */
    public void createCourse(Course course) {
        requireAdmin();

        log.info(
                "ADMIN {} creating course '{}'",
                userContext.getUserId(),
                course.getTitle()
        );

        repository.insert(course);
    }

    /**
     * Updates an existing course.
     *
     * Access:
     * - ADMIN only
     *
     * @throws NotFoundException if course does not exist
     */
    public void updateCourse(Course course) {
        requireAdmin();

        if (repository.findById(course.getCourseId()).isEmpty()) {

            log.warn(
                    "Course {} not found for update (user={})",
                    course.getCourseId(),
                    userContext.getUserId()
            );

            throw new NotFoundException(
                    "Course not found: " + course.getCourseId()
            );
        }

        log.info(
                "ADMIN {} updating course {}",
                userContext.getUserId(),
                course.getCourseId()
        );

        repository.update(course);
    }

    /**
     * Deletes a course.
     *
     * Access:
     * - ADMIN only
     *
     * @throws NotFoundException if course does not exist
     */
    public void deleteCourse(String courseId) {
        requireAdmin();

        if (repository.findById(courseId).isEmpty()) {

            log.warn(
                    "Course {} not found for delete (user={})",
                    courseId,
                    userContext.getUserId()
            );

            throw new NotFoundException(
                    "Course not found: " + courseId
            );
        }

        log.info(
                "ADMIN {} deleting course {}",
                userContext.getUserId(),
                courseId
        );

        repository.delete(courseId);
    }

    // ==================================================
    // ACCESS POLICY
    // ==================================================

    /**
     * Enforces ADMIN-only access.
     *
     * Centralized role check for write operations.
     *
     * IMPORTANT:
     * - Controllers MUST NOT check roles
     * - Repository MUST NOT check roles
     * - All access control lives here
     *
     * @throws ForbiddenException if user is not ADMIN
     */
    private void requireAdmin() {
        if (!userContext.hasRole("ADMIN")) {

            log.warn(
                    "Forbidden access: user {} tried ADMIN operation",
                    userContext.getUserId()
            );

            throw new ForbiddenException("ADMIN role required");
        }
    }
}
