package at.campus.backend.modules.watch.model;

/**
 * DTO for watch subscription.
 */
public class WatchDto {

    private String id;
    private WatchTargetType targetType;
    private String targetId;
    private boolean notificationsEnabled;
    private boolean isWatching;

    public WatchDto() {
    }

    public WatchDto(String id, WatchTargetType targetType, String targetId, boolean notificationsEnabled, boolean isWatching) {
        this.id = id;
        this.targetType = targetType;
        this.targetId = targetId;
        this.notificationsEnabled = notificationsEnabled;
        this.isWatching = isWatching;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WatchTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(WatchTargetType targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public boolean isWatching() {
        return isWatching;
    }

    public void setWatching(boolean watching) {
        isWatching = watching;
    }
}
