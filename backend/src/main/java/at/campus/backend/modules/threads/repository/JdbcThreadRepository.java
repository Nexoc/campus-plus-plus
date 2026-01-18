package at.campus.backend.modules.threads.repository;

import at.campus.backend.modules.threads.model.Thread;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JDBC implementation of ThreadRepository.
 */
@Repository
public class JdbcThreadRepository implements ThreadRepository {

    private final JdbcTemplate jdbc;

    public JdbcThreadRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Thread> findByCourseId(UUID courseId) {
        String sql = """
            SELECT id, course_id, title, content, created_by, created_by_name, created_at
            FROM app.threads
            WHERE course_id = ?
            ORDER BY created_at DESC
        """;
        return jdbc.query(sql, new ThreadRowMapper(), courseId);
    }

    @Override
    public Optional<Thread> findById(UUID id) {
        String sql = """
            SELECT id, course_id, title, content, created_by, created_by_name, created_at
            FROM app.threads
            WHERE id = ?
        """;
        List<Thread> results = jdbc.query(sql, new ThreadRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public void save(Thread thread) {
        String sql = """
            INSERT INTO app.threads (id, course_id, title, content, created_by, created_by_name, created_at)
            VALUES (?, ?, ?, ?, ?, ?, now())
        """;
        jdbc.update(sql,
            thread.getId(),
            thread.getCourseId(),
            thread.getTitle(),
            thread.getContent(),
            thread.getCreatedBy(),
            thread.getCreatedByName()
        );
    }

    @Override
    public void update(Thread thread) {
        String sql = """
            UPDATE app.threads
            SET title = ?, content = ?
            WHERE id = ?
        """;
        jdbc.update(sql,
            thread.getTitle(),
            thread.getContent(),
            thread.getId()
        );
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM app.threads WHERE id = ?";
        jdbc.update(sql, id);
    }

    // RowMapper

    private static class ThreadRowMapper implements RowMapper<Thread> {
        @Override
        public Thread mapRow(ResultSet rs, int rowNum) throws SQLException {
            Thread thread = new Thread();
            thread.setId((UUID) rs.getObject("id"));
            thread.setCourseId((UUID) rs.getObject("course_id"));
            thread.setTitle(rs.getString("title"));
            thread.setContent(rs.getString("content"));
            thread.setCreatedBy((UUID) rs.getObject("created_by"));
            thread.setCreatedByName(rs.getString("created_by_name"));
            thread.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return thread;
        }
    }
}
