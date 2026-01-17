package at.campus.backend.modules.coursematerials.repository;

import at.campus.backend.modules.coursematerials.model.CourseMaterial;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository abstraction for CourseMaterial persistence.
 *
 * SQL-first design. No JPA.
 */
public interface CourseMaterialRepository {

    void insert(CourseMaterial material);

    List<CourseMaterial> findByCourseId(UUID courseId);

    Optional<CourseMaterial> findById(UUID id);


    void updateMetadata(UUID id, String title, String description);

    void deleteById(UUID id);
}
