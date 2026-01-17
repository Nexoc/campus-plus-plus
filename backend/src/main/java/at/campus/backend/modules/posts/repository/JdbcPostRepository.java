package at.campus.backend.modules.posts.repository;

import at.campus.backend.modules.posts.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JDBC implementation of PostRepository.
 */
@Repository
public class JdbcPostRepository implements PostRepository {

    private final JdbcTemplate jdbc;

    public JdbcPostRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Post> findByThreadId(UUID threadId) {
        String sql = """
            SELECT id, thread_id, user_id, user_name, content, created_at, updated_at
            FROM app.posts
            WHERE thread_id = ?
            ORDER BY created_at ASC
        """;
        return jdbc.query(sql, new PostRowMapper(), threadId);
    }

    @Override
    public Optional<Post> findById(UUID id) {
        String sql = """
            SELECT id, thread_id, user_id, user_name, content, created_at, updated_at
            FROM app.posts
            WHERE id = ?
        """;
        List<Post> results = jdbc.query(sql, new PostRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public void save(Post post) {
        String sql = """
            INSERT INTO app.posts (id, thread_id, user_id, user_name, content, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, now(), now())
        """;
        jdbc.update(sql,
            post.getId(),
            post.getThreadId(),
            post.getUserId(),
            post.getUserName(),
            post.getContent()
        );
    }

    @Override
    public void update(Post post) {
        String sql = """
            UPDATE app.posts
            SET content = ?, updated_at = now()
            WHERE id = ?
        """;
        jdbc.update(sql,
            post.getContent(),
            post.getId()
        );
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM app.posts WHERE id = ?";
        jdbc.update(sql, id);
    }

    @Override
    public Integer getPostCountByThreadId(UUID threadId) {
        String sql = "SELECT COUNT(*) FROM app.posts WHERE thread_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, threadId);
        return count != null ? count : 0;
    }

    // RowMapper

    private static class PostRowMapper implements RowMapper<Post> {
        @Override
        public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
            Post post = new Post();
            post.setId((UUID) rs.getObject("id"));
            post.setThreadId((UUID) rs.getObject("thread_id"));
            post.setUserId((UUID) rs.getObject("user_id"));
            post.setUserName(rs.getString("user_name"));
            post.setContent(rs.getString("content"));
            post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            post.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return post;
        }
    }
}
