package at.campus.backend.modules.courses.repository;

import at.campus.backend.modules.courses.model.Course;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository abstraction for Course persistence.
 *
 * SQL-first design.
 * No JPA.
 */
public interface CourseRepository {

    // ==================================================
    // READ
    // ==================================================

    List<Course> findAll();

    Optional<Course> findById(UUID courseId);

    List<Course> findFiltered(UUID studyProgramId, Integer ects);

    // ==================================================
    // WRITE (ADMIN only – enforced in service)
    // ==================================================

    void insert(Course course);

    void update(Course course);

    boolean deleteById(UUID id);
}
