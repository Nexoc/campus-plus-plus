package at.campus.backend.modules.watch.api;

import at.campus.backend.modules.watch.model.WatchDto;
import at.campus.backend.modules.watch.model.WatchTargetType;
import at.campus.backend.modules.watch.service.WatchService;
import at.campus.backend.security.UserContext;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Public controller for watch status (read-only).
 * Available to all users.
 */
@RestController
@RequestMapping("/api/public/watch")
public class WatchPublicController {

    private final WatchService watchService;
    private final UserContext userContext;

    public WatchPublicController(WatchService watchService, UserContext userContext) {
        this.watchService = watchService;
        this.userContext = userContext;
    }

    /**
     * Get watch status for a target.
     * Returns whether the current user (if authenticated) is watching the target.
     */
    @GetMapping("/status")
    public WatchDto getWatchStatus(
        @RequestParam WatchTargetType targetType,
        @RequestParam String targetId
    ) {
        UUID userId = userContext.getUserId() != null 
            ? UUID.fromString(userContext.getUserId()) 
            : null;
        UUID targetUuid = UUID.fromString(targetId);
        
        return watchService.getWatchStatus(userId, targetType, targetUuid);
    }
}
