package at.campus.backend.modules.watch.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Watch subscription domain model.
 *
 * Represents a user's subscription to watch a course or thread.
 */
public class WatchSubscription {

    private UUID id;
    private UUID userId;
    private WatchTargetType targetType;
    private UUID targetId;
    private boolean notificationsEnabled;
    private LocalDateTime createdAt;

    // Constructors

    public WatchSubscription() {
    }

    public WatchSubscription(UUID id, UUID userId, WatchTargetType targetType, UUID targetId, boolean notificationsEnabled) {
        this.id = id;
        this.userId = userId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.notificationsEnabled = notificationsEnabled;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public WatchTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(WatchTargetType targetType) {
        this.targetType = targetType;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public void setTargetId(UUID targetId) {
        this.targetId = targetId;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
