package at.campus.backend.modules.coursematerials.repository;

import at.campus.backend.modules.coursematerials.model.CourseMaterial;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JDBC implementation of CourseMaterialRepository.
 */
@Repository
public class JdbcCourseMaterialRepository implements CourseMaterialRepository {

    private final JdbcTemplate jdbc;

    public JdbcCourseMaterialRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void insert(CourseMaterial m) {
        String sql = """
            INSERT INTO app.course_materials
                (id, course_id, uploader_id, title, description,
                 original_filename, content_type, size_bytes, storage_key, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbc.update(sql,
                m.getId(),
                m.getCourseId(),
                m.getUploaderId(),
                m.getTitle(),
                m.getDescription(),
                m.getOriginalFilename(),
                m.getContentType(),
                m.getSizeBytes(),
                m.getStorageKey(),
                Timestamp.valueOf(m.getCreatedAt())
        );
    }

    @Override
    public List<CourseMaterial> findByCourseId(UUID courseId) {
        String sql = """
            SELECT id, course_id, uploader_id, title, description,
                   original_filename, content_type, size_bytes, storage_key, created_at
            FROM app.course_materials
            WHERE course_id = ?
            ORDER BY created_at DESC
        """;
        return jdbc.query(sql, rowMapper(), courseId);
    }

    private RowMapper<CourseMaterial> rowMapper() {
        return (rs, rowNum) -> {
            CourseMaterial m = new CourseMaterial();
            m.setId((UUID) rs.getObject("id"));
            m.setCourseId((UUID) rs.getObject("course_id"));
            m.setUploaderId((UUID) rs.getObject("uploader_id"));
            m.setTitle(rs.getString("title"));
            m.setDescription(rs.getString("description"));
            m.setOriginalFilename(rs.getString("original_filename"));
            m.setContentType(rs.getString("content_type"));
            m.setSizeBytes(rs.getLong("size_bytes"));
            m.setStorageKey(rs.getString("storage_key"));

            Timestamp ts = rs.getTimestamp("created_at");
            m.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());

            return m;
        };
    }

    @Override
    public Optional<CourseMaterial> findById(UUID id) {
        String sql = """
        SELECT id, course_id, uploader_id, title, description,
               original_filename, content_type, size_bytes, storage_key, created_at
        FROM app.course_materials
        WHERE id = ?
    """;

        try {
            CourseMaterial m = jdbc.queryForObject(sql, rowMapper(), id);
            return Optional.ofNullable(m);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateMetadata(UUID id, String title, String description) {
        jdbc.update("""
        UPDATE app.course_materials
        SET title = ?, description = ?
        WHERE id = ?
    """, title, description, id);
    }

    @Override
    public void deleteById(UUID id) {
        jdbc.update("DELETE FROM app.course_materials WHERE id = ?", id);
    }


}
