package at.campus.backend.modules.watch.repository;

import at.campus.backend.modules.watch.model.WatchSubscription;
import at.campus.backend.modules.watch.model.WatchTargetType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JDBC implementation of WatchRepository.
 */
@Repository
public class JdbcWatchRepository implements WatchRepository {

    private final JdbcTemplate jdbc;

    public JdbcWatchRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public WatchSubscription create(WatchSubscription subscription) {
        String sql = """
            INSERT INTO app.watch_subscriptions (id, user_id, target_type, target_id, notifications_enabled, created_at)
            VALUES (?, ?, ?::VARCHAR, ?, ?, NOW())
            ON CONFLICT (user_id, target_type, target_id) 
            DO UPDATE SET notifications_enabled = EXCLUDED.notifications_enabled
        """;
        
        jdbc.update(sql,
            subscription.getId(),
            subscription.getUserId(),
            subscription.getTargetType().name(),
            subscription.getTargetId(),
            subscription.isNotificationsEnabled()
        );
        
        return subscription;
    }

    @Override
    public void delete(UUID userId, WatchTargetType targetType, UUID targetId) {
        String sql = """
            DELETE FROM app.watch_subscriptions
            WHERE user_id = ? AND target_type = ?::VARCHAR AND target_id = ?
        """;
        
        jdbc.update(sql, userId, targetType.name(), targetId);
    }

    @Override
    public Optional<WatchSubscription> find(UUID userId, WatchTargetType targetType, UUID targetId) {
        String sql = """
            SELECT id, user_id, target_type, target_id, notifications_enabled, created_at
            FROM app.watch_subscriptions
            WHERE user_id = ? AND target_type = ?::VARCHAR AND target_id = ?
        """;
        
        List<WatchSubscription> results = jdbc.query(sql, new WatchSubscriptionRowMapper(),
            userId, targetType.name(), targetId);
        
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public boolean isWatching(UUID userId, WatchTargetType targetType, UUID targetId) {
        String sql = """
            SELECT COUNT(*)
            FROM app.watch_subscriptions
            WHERE user_id = ? AND target_type = ?::VARCHAR AND target_id = ?
        """;
        
        Integer count = jdbc.queryForObject(sql, Integer.class,
            userId, targetType.name(), targetId);
        
        return count != null && count > 0;
    }

    @Override
    public List<WatchSubscription> findByUserId(UUID userId) {
        String sql = """
            SELECT id, user_id, target_type, target_id, notifications_enabled, created_at
            FROM app.watch_subscriptions
            WHERE user_id = ?
            ORDER BY created_at DESC
        """;
        
        return jdbc.query(sql, new WatchSubscriptionRowMapper(), userId);
    }

    @Override
    public List<UUID> findUsersWatchingTarget(WatchTargetType targetType, UUID targetId) {
        String sql = """
            SELECT DISTINCT ws.user_id
            FROM app.watch_subscriptions ws
            LEFT JOIN app.notification_settings ns ON ws.user_id = ns.user_id
            WHERE ws.target_type = ?::VARCHAR 
            AND ws.target_id = ?
            AND ws.notifications_enabled = true
            AND (ns.email_enabled IS NULL OR ns.email_enabled = true)
        """;
        
        return jdbc.query(sql, 
            (rs, rowNum) -> (UUID) rs.getObject("user_id"),
            targetType.name(), targetId);
    }

    private static class WatchSubscriptionRowMapper implements RowMapper<WatchSubscription> {
        @Override
        public WatchSubscription mapRow(ResultSet rs, int rowNum) throws SQLException {
            WatchSubscription subscription = new WatchSubscription();
            subscription.setId((UUID) rs.getObject("id"));
            subscription.setUserId((UUID) rs.getObject("user_id"));
            subscription.setTargetType(WatchTargetType.valueOf(rs.getString("target_type")));
            subscription.setTargetId((UUID) rs.getObject("target_id"));
            subscription.setNotificationsEnabled(rs.getBoolean("notifications_enabled"));
            subscription.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return subscription;
        }
    }
}
