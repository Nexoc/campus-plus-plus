package at.campus.backend.modules.comments.repository;

import at.campus.backend.modules.comments.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JDBC implementation of CommentRepository.
 */
@Repository
public class JdbcCommentRepository implements CommentRepository {

    private final JdbcTemplate jdbc;

    public JdbcCommentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Comment> findByPostId(UUID postId) {
        String sql = """
            SELECT id, post_id, user_id, user_name, content, created_at, updated_at
            FROM app.comments
            WHERE post_id = ?
            ORDER BY created_at ASC
        """;
        return jdbc.query(sql, new CommentRowMapper(), postId);
    }

    @Override
    public Optional<Comment> findById(UUID id) {
        String sql = """
            SELECT id, post_id, user_id, user_name, content, created_at, updated_at
            FROM app.comments
            WHERE id = ?
        """;
        List<Comment> results = jdbc.query(sql, new CommentRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public void save(Comment comment) {
        String sql = """
            INSERT INTO app.comments (id, post_id, user_id, user_name, content, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, now(), now())
        """;
        jdbc.update(sql,
            comment.getId(),
            comment.getPostId(),
            comment.getUserId(),
            comment.getUserName(),
            comment.getContent()
        );
    }

    @Override
    public void update(Comment comment) {
        String sql = """
            UPDATE app.comments
            SET content = ?, updated_at = now()
            WHERE id = ?
        """;
        jdbc.update(sql,
            comment.getContent(),
            comment.getId()
        );
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM app.comments WHERE id = ?";
        jdbc.update(sql, id);
    }

    @Override
    public Integer getCommentCountByPostId(UUID postId) {
        String sql = "SELECT COUNT(*) FROM app.comments WHERE post_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, postId);
        return count != null ? count : 0;
    }

    // RowMapper

    private static class CommentRowMapper implements RowMapper<Comment> {
        @Override
        public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Comment comment = new Comment();
            comment.setId((UUID) rs.getObject("id"));
            comment.setPostId((UUID) rs.getObject("post_id"));
            comment.setUserId((UUID) rs.getObject("user_id"));
            comment.setUserName(rs.getString("user_name"));
            comment.setContent(rs.getString("content"));
            comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            comment.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return comment;
        }
    }
}
