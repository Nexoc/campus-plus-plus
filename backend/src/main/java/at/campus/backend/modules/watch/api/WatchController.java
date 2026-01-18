package at.campus.backend.modules.watch.api;

import at.campus.backend.modules.watch.model.WatchDto;
import at.campus.backend.modules.watch.model.WatchRequest;
import at.campus.backend.modules.watch.model.WatchTargetType;
import at.campus.backend.modules.watch.service.WatchService;
import at.campus.backend.security.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for watch subscriptions.
 * Requires authentication.
 */
@RestController
@RequestMapping("/api/watch")
public class WatchController {

    private final WatchService watchService;
    private final UserContext userContext;

    public WatchController(WatchService watchService, UserContext userContext) {
        this.watchService = watchService;
        this.userContext = userContext;
    }

    /**
     * Watch a course or thread.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchDto watch(@RequestBody WatchRequest request) {
        UUID userId = UUID.fromString(userContext.getUserId());
        UUID targetId = UUID.fromString(request.getTargetId());
        
        return watchService.watch(userId, request.getTargetType(), targetId, request.isNotificationsEnabled());
    }

    /**
     * Unwatch a course or thread.
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unwatch(@RequestBody WatchRequest request) {
        UUID userId = UUID.fromString(userContext.getUserId());
        UUID targetId = UUID.fromString(request.getTargetId());
        
        watchService.unwatch(userId, request.getTargetType(), targetId);
    }

    /**
     * Get user's watch list.
     */
    @GetMapping
    public List<WatchDto> getWatchList() {
        UUID userId = UUID.fromString(userContext.getUserId());
        return watchService.getUserWatchList(userId);
    }

    /**
     * Check watch status for a specific target.
     */
    @GetMapping("/status")
    public WatchDto getWatchStatus(
        @RequestParam WatchTargetType targetType,
        @RequestParam String targetId
    ) {
        UUID userId = UUID.fromString(userContext.getUserId());
        UUID targetUuid = UUID.fromString(targetId);
        
        return watchService.getWatchStatus(userId, targetType, targetUuid);
    }
}
