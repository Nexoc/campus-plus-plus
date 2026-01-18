package at.campus.backend.modules.watch.service;

import at.campus.backend.modules.watch.model.*;
import at.campus.backend.modules.watch.repository.WatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing watch subscriptions.
 */
@Service
public class WatchService {

    private final WatchRepository watchRepository;

    public WatchService(WatchRepository watchRepository) {
        this.watchRepository = watchRepository;
    }

    /**
     * Watch a target.
     */
    public WatchDto watch(UUID userId, WatchTargetType targetType, UUID targetId, boolean notificationsEnabled) {
        WatchSubscription subscription = new WatchSubscription(
            UUID.randomUUID(),
            userId,
            targetType,
            targetId,
            notificationsEnabled
        );
        
        watchRepository.create(subscription);
        
        return toDto(subscription, true);
    }

    /**
     * Unwatch a target.
     */
    public void unwatch(UUID userId, WatchTargetType targetType, UUID targetId) {
        watchRepository.delete(userId, targetType, targetId);
    }

    /**
     * Check if user is watching a target.
     */
    public WatchDto getWatchStatus(UUID userId, WatchTargetType targetType, UUID targetId) {
        if (userId == null) {
            return new WatchDto(null, targetType, targetId.toString(), false, false);
        }

        var subscription = watchRepository.find(userId, targetType, targetId);
        
        if (subscription.isPresent()) {
            return toDto(subscription.get(), true);
        } else {
            return new WatchDto(null, targetType, targetId.toString(), false, false);
        }
    }

    /**
     * Get all watch subscriptions for a user.
     */
    public List<WatchDto> getUserWatchList(UUID userId) {
        return watchRepository.findByUserId(userId).stream()
            .map(sub -> toDto(sub, true))
            .collect(Collectors.toList());
    }

    /**
     * Get users watching a target (for notifications).
     */
    public List<UUID> getUsersWatchingTarget(WatchTargetType targetType, UUID targetId) {
        return watchRepository.findUsersWatchingTarget(targetType, targetId);
    }

    private WatchDto toDto(WatchSubscription subscription, boolean isWatching) {
        return new WatchDto(
            subscription.getId().toString(),
            subscription.getTargetType(),
            subscription.getTargetId().toString(),
            subscription.isNotificationsEnabled(),
            isWatching
        );
    }
}
