package at.campus.backend.modules.watch.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Notification settings for a user.
 */
public class NotificationSettings {

    private UUID userId;
    private boolean emailEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NotificationSettings() {
    }

    public NotificationSettings(UUID userId, boolean emailEnabled) {
        this.userId = userId;
        this.emailEnabled = emailEnabled;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
