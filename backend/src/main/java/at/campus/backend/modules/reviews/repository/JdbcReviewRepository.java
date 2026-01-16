package at.campus.backend.modules.reviews.repository;

import at.campus.backend.modules.reviews.model.Review;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JDBC implementation of ReviewRepository.
 */
@Repository
public class JdbcReviewRepository implements ReviewRepository {

    private final JdbcTemplate jdbc;

    public JdbcReviewRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Review> findByCourseId(UUID courseId) {
        String sql = """
            SELECT id, user_id, course_id, rating, difficulty, workload, satisfaction,
                   prior_requirements, exam_info, text, created_at, updated_at,
                   moderation_flagged, moderation_reason
            FROM app.reviews
            WHERE course_id = ?
            ORDER BY created_at DESC
        """;
        return jdbc.query(sql, new ReviewRowMapper(), courseId);
    }

    @Override
    public Optional<Review> findById(UUID id) {
        String sql = """
            SELECT id, user_id, course_id, rating, difficulty, workload, satisfaction,
                   prior_requirements, exam_info, text, created_at, updated_at,
                   moderation_flagged, moderation_reason
            FROM app.reviews
            WHERE id = ?
        """;
        List<Review> results = jdbc.query(sql, new ReviewRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Review> findAll() {
        String sql = """
            SELECT id, user_id, course_id, rating, difficulty, workload, satisfaction,
                   prior_requirements, exam_info, text, created_at, updated_at,
                   moderation_flagged, moderation_reason
            FROM app.reviews
            ORDER BY created_at DESC
        """;
        return jdbc.query(sql, new ReviewRowMapper());
    }

    @Override
    public void save(Review review) {
        String sql = """
            INSERT INTO app.reviews (id, user_id, course_id, rating, difficulty, workload, satisfaction,
                                     prior_requirements, exam_info, text, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now())
        """;
        jdbc.update(sql,
            review.getId(),
            review.getUserId(),
            review.getCourseId(),
            review.getRating(),
            review.getDifficulty(),
            review.getWorkload(),
            review.getSatisfaction(),
            review.getPriorRequirements(),
            review.getExamInfo(),
            review.getText()
        );
    }

    @Override
    public void update(Review review) {
        String sql = """
            UPDATE app.reviews
            SET rating = ?, difficulty = ?, workload = ?, satisfaction = ?,
                prior_requirements = ?, exam_info = ?, text = ?,
                moderation_flagged = ?, moderation_reason = ?, updated_at = now()
            WHERE id = ?
        """;
        jdbc.update(sql,
            review.getRating(),
            review.getDifficulty(),
            review.getWorkload(),
            review.getSatisfaction(),
            review.getPriorRequirements(),
            review.getExamInfo(),
            review.getText(),
            review.isModerationFlagged(),
            review.getModerationReason(),
            review.getId()
        );
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM app.reviews WHERE id = ?";
        jdbc.update(sql, id);
    }

    @Override
    public boolean existsByUserIdAndCourseId(UUID userId, UUID courseId) {
        String sql = "SELECT COUNT(*) FROM app.reviews WHERE user_id = ? AND course_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId, courseId);
        return count != null && count > 0;
    }

    /**
     * RowMapper for Review entity.
     */
    private static class ReviewRowMapper implements RowMapper<Review> {
        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            Review review = new Review();
            review.setId((UUID) rs.getObject("id"));
            review.setUserId((UUID) rs.getObject("user_id"));
            review.setCourseId((UUID) rs.getObject("course_id"));
            review.setRating(rs.getInt("rating"));
            
            // Handle nullable integers
            Integer difficulty = (Integer) rs.getObject("difficulty");
            review.setDifficulty(difficulty);
            
            Integer workload = (Integer) rs.getObject("workload");
            review.setWorkload(workload);
            
            Integer satisfaction = (Integer) rs.getObject("satisfaction");
            review.setSatisfaction(satisfaction);
            
            review.setPriorRequirements(rs.getString("prior_requirements"));
            review.setExamInfo(rs.getString("exam_info"));
            review.setText(rs.getString("text"));
            review.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            review.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            review.setModerationFlagged(rs.getBoolean("moderation_flagged"));
            review.setModerationReason(rs.getString("moderation_reason"));
            return review;
        }
    }
}
