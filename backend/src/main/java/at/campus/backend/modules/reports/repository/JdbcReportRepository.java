package at.campus.backend.modules.reports.repository;

import at.campus.backend.modules.reports.model.Report;
import at.campus.backend.modules.reports.model.ReportReason;
import at.campus.backend.modules.reports.model.ReportStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JDBC implementation of ReportRepository.
 */
@Repository
public class JdbcReportRepository implements ReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Report> findAll() {
        String sql = "SELECT * FROM app.reports ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new ReportRowMapper());
    }

    @Override
    public Optional<Report> findById(UUID id) {
        String sql = "SELECT * FROM app.reports WHERE id = ?";
        List<Report> results = jdbcTemplate.query(sql, new ReportRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Report> findByStatus(ReportStatus status) {
        String sql = "SELECT * FROM app.reports WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new ReportRowMapper(), status.name());
    }

    @Override
    public List<Report> findByTargetType(String targetType) {
        String sql = "SELECT * FROM app.reports WHERE target_type = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new ReportRowMapper(), targetType);
    }

    @Override
    public List<Report> findByTargetTypeAndTargetId(String targetType, UUID targetId) {
        String sql = "SELECT * FROM app.reports WHERE target_type = ? AND target_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new ReportRowMapper(), targetType, targetId);
    }

    @Override
    public List<Report> findByUserId(UUID userId) {
        String sql = "SELECT * FROM app.reports WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new ReportRowMapper(), userId);
    }

    @Override
    public boolean existsByUserIdAndTargetTypeAndTargetId(UUID userId, String targetType, UUID targetId) {
        String sql = "SELECT COUNT(*) FROM app.reports WHERE user_id = ? AND target_type = ? AND target_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, targetType, targetId);
        return count != null && count > 0;
    }

    @Override
    public void save(Report report) {
        // Note: Database stores reason as TEXT, comment is stored in moderator_notes for now
        // In future migration, add a separate comment column
        String sql = """
            INSERT INTO app.reports (
                id, target_type, target_id, user_id, reason, status, created_at, resolved_at, moderator_notes
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                status = EXCLUDED.status,
                resolved_at = EXCLUDED.resolved_at,
                moderator_notes = EXCLUDED.moderator_notes
            """;
        
        // Combine comment and moderator notes (comment comes first)
        String combinedNotes = report.getComment();
        if (report.getModeratorNotes() != null && !report.getModeratorNotes().isBlank()) {
            combinedNotes = (combinedNotes != null ? combinedNotes + "\n---\n" : "") + report.getModeratorNotes();
        }
        
        jdbcTemplate.update(sql,
            report.getId(),
            report.getTargetType(),
            report.getTargetId(),
            report.getUserId(),
            report.getReason() != null ? report.getReason().name() : null,
            report.getStatus().name(),
            Timestamp.from(report.getCreatedAt().toInstant()),
            report.getResolvedAt() != null ? Timestamp.from(report.getResolvedAt().toInstant()) : null,
            combinedNotes
        );
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM app.reports WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    /**
     * RowMapper for Report entities.
     */
    private static class ReportRowMapper implements RowMapper<Report> {
        @Override
        public Report mapRow(ResultSet rs, int rowNum) throws SQLException {
            Report report = new Report();
            report.setId((UUID) rs.getObject("id"));
            report.setTargetType(rs.getString("target_type"));
            report.setTargetId((UUID) rs.getObject("target_id"));
            report.setUserId((UUID) rs.getObject("user_id"));
            
            // Parse reason as enum
            String reasonStr = rs.getString("reason");
            if (reasonStr != null) {
                try {
                    report.setReason(ReportReason.valueOf(reasonStr));
                } catch (IllegalArgumentException e) {
                    // Fallback for old data that might not be enum values
                    report.setReason(ReportReason.OTHER);
                }
            }
            
            report.setStatus(ReportStatus.valueOf(rs.getString("status")));
            
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                report.setCreatedAt(OffsetDateTime.ofInstant(createdAt.toInstant(), ZoneOffset.UTC));
            }
            
            Timestamp resolvedAt = rs.getTimestamp("resolved_at");
            if (resolvedAt != null) {
                report.setResolvedAt(OffsetDateTime.ofInstant(resolvedAt.toInstant(), ZoneOffset.UTC));
            }
            
            // moderator_notes might contain both comment and moderator notes
            // For now, we'll put everything in moderator_notes
            report.setModeratorNotes(rs.getString("moderator_notes"));
            
            return report;
        }
    }
}
