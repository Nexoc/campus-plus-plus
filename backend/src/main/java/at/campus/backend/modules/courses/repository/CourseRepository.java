package at.campus.backend.modules.courses.repository;

import at.campus.backend.modules.courses.model.Course;

import java.util.List;
import java.util.Optional;

/**
 * Repository abstraction for Course persistence.
 *
 * SQL-first design.
 * No JPA.
 */
public interface CourseRepository {

    // READ
    List<Course> findAll();

    Optional<Course> findById(String courseId);

    List<Course> findFiltered(String studyProgramId, Integer ects);

    // WRITE (ADMIN only – enforced in service)
    void insert(Course course);

    void update(Course course);

    void delete(String courseId);
}
