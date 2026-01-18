package at.campus.backend.modules.reactions.repository;

import at.campus.backend.modules.reactions.model.Reaction;
import at.campus.backend.modules.reactions.model.ReactionType;
import at.campus.backend.modules.reactions.model.TargetType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JDBC implementation of ReactionRepository.
 */
@Repository
public class JdbcReactionRepository implements ReactionRepository {

    private final JdbcTemplate jdbc;

    public JdbcReactionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Reaction addReaction(Reaction reaction) {
        String sql = """
            INSERT INTO app.reactions (id, user_id, target_type, target_id, reaction_type, created_at)
            VALUES (?, ?, ?::VARCHAR, ?, ?::VARCHAR, NOW())
            ON CONFLICT (user_id, target_type, target_id, reaction_type) DO NOTHING
        """;
        
        jdbc.update(sql,
            reaction.getId(),
            reaction.getUserId(),
            reaction.getTargetType().name(),
            reaction.getTargetId(),
            reaction.getReactionType().name()
        );
        
        return reaction;
    }

    @Override
    public void removeReaction(UUID userId, TargetType targetType, UUID targetId, ReactionType reactionType) {
        String sql = """
            DELETE FROM app.reactions
            WHERE user_id = ? AND target_type = ?::VARCHAR AND target_id = ? AND reaction_type = ?::VARCHAR
        """;
        
        jdbc.update(sql,
            userId,
            targetType.name(),
            targetId,
            reactionType.name()
        );
    }

    @Override
    public Optional<Reaction> findReaction(UUID userId, TargetType targetType, UUID targetId, ReactionType reactionType) {
        String sql = """
            SELECT id, user_id, target_type, target_id, reaction_type, created_at
            FROM app.reactions
            WHERE user_id = ? AND target_type = ?::VARCHAR AND target_id = ? AND reaction_type = ?::VARCHAR
        """;
        
        List<Reaction> results = jdbc.query(sql, new ReactionRowMapper(),
            userId, targetType.name(), targetId, reactionType.name());
        
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public int countReactions(TargetType targetType, UUID targetId, ReactionType reactionType) {
        String sql = """
            SELECT COUNT(*)
            FROM app.reactions
            WHERE target_type = ?::VARCHAR AND target_id = ? AND reaction_type = ?::VARCHAR
        """;
        
        Integer count = jdbc.queryForObject(sql, Integer.class,
            targetType.name(), targetId, reactionType.name());
        
        return count != null ? count : 0;
    }

    @Override
    public boolean hasUserReacted(UUID userId, TargetType targetType, UUID targetId, ReactionType reactionType) {
        String sql = """
            SELECT COUNT(*)
            FROM app.reactions
            WHERE user_id = ? AND target_type = ?::VARCHAR AND target_id = ? AND reaction_type = ?::VARCHAR
        """;
        
        Integer count = jdbc.queryForObject(sql, Integer.class,
            userId, targetType.name(), targetId, reactionType.name());
        
        return count != null && count > 0;
    }

    private static class ReactionRowMapper implements RowMapper<Reaction> {
        @Override
        public Reaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Reaction reaction = new Reaction();
            reaction.setId((UUID) rs.getObject("id"));
            reaction.setUserId((UUID) rs.getObject("user_id"));
            reaction.setTargetType(TargetType.valueOf(rs.getString("target_type")));
            reaction.setTargetId((UUID) rs.getObject("target_id"));
            reaction.setReactionType(ReactionType.valueOf(rs.getString("reaction_type")));
            reaction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return reaction;
        }
    }
}
