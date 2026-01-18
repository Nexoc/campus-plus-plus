package at.campus.backend.modules.watch.service;

import at.campus.backend.modules.watch.model.WatchTargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Simplified notification service for sending email notifications.
 * 
 * NOTE: This is a simplified implementation for a student project.
 * In production, you would integrate with a real email service (SMTP, SendGrid, etc.)
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    /**
     * Send notification about new review.
     */
    public void notifyNewReview(UUID courseId, UUID reviewId, List<UUID> watcherUserIds) {
        if (watcherUserIds.isEmpty()) {
            return;
        }

        logger.info("📧 [NOTIFICATION] New review on course {} - would notify {} users: {}", 
            courseId, watcherUserIds.size(), watcherUserIds);
        
        // In a real implementation, you would:
        // 1. Fetch user email addresses from auth service
        // 2. Compose email with review details
        // 3. Send via SMTP/email service
        // 4. Handle failures gracefully
        
        // For demo purposes, we just log
        watcherUserIds.forEach(userId -> {
            logger.info("  → Would send email to user: {}", userId);
        });
    }

    /**
     * Send notification about new thread.
     */
    public void notifyNewThread(UUID courseId, UUID threadId, String threadTitle, List<UUID> watcherUserIds) {
        if (watcherUserIds.isEmpty()) {
            return;
        }

        logger.info("📧 [NOTIFICATION] New thread '{}' on course {} - would notify {} users: {}", 
            threadTitle, courseId, watcherUserIds.size(), watcherUserIds);
        
        watcherUserIds.forEach(userId -> {
            logger.info("  → Would send email to user: {}", userId);
        });
    }

    /**
     * Send notification about new post in thread.
     */
    public void notifyNewPost(UUID threadId, UUID postId, List<UUID> watcherUserIds) {
        if (watcherUserIds.isEmpty()) {
            return;
        }

        logger.info("📧 [NOTIFICATION] New post on thread {} - would notify {} users: {}", 
            threadId, watcherUserIds.size(), watcherUserIds);
        
        watcherUserIds.forEach(userId -> {
            logger.info("  → Would send email to user: {}", userId);
        });
    }

    /**
     * Generic notification method.
     */
    public void notifyActivity(WatchTargetType targetType, UUID targetId, String activityDescription, List<UUID> watcherUserIds) {
        if (watcherUserIds.isEmpty()) {
            return;
        }

        logger.info("📧 [NOTIFICATION] {} on {} {} - would notify {} users", 
            activityDescription, targetType, targetId, watcherUserIds.size());
        
        watcherUserIds.forEach(userId -> {
            logger.info("  → Would send email to user: {}", userId);
        });
    }
}
