package at.campus.backend.modules.watch.model;

/**
 * Request to watch a target.
 */
public class WatchRequest {

    private WatchTargetType targetType;
    private String targetId;
    private boolean notificationsEnabled = true;

    public WatchRequest() {
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
}
