package at.campus.backend.modules.watch.repository;

import at.campus.backend.modules.watch.model.WatchSubscription;
import at.campus.backend.modules.watch.model.WatchTargetType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for watch subscriptions.
 */
public interface WatchRepository {

    /**
     * Create a watch subscription.
     */
    WatchSubscription create(WatchSubscription subscription);

    /**
     * Delete a watch subscription.
     */
    void delete(UUID userId, WatchTargetType targetType, UUID targetId);

    /**
     * Find a watch subscription.
     */
    Optional<WatchSubscription> find(UUID userId, WatchTargetType targetType, UUID targetId);

    /**
     * Check if user is watching a target.
     */
    boolean isWatching(UUID userId, WatchTargetType targetType, UUID targetId);

    /**
     * Get all watch subscriptions for a user.
     */
    List<WatchSubscription> findByUserId(UUID userId);

    /**
     * Get all users watching a target.
     */
    List<UUID> findUsersWatchingTarget(WatchTargetType targetType, UUID targetId);
}
