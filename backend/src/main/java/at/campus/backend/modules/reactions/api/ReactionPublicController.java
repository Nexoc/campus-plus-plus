package at.campus.backend.modules.reactions.api;

import at.campus.backend.modules.reactions.model.ReactionCountDto;
import at.campus.backend.modules.reactions.model.TargetType;
import at.campus.backend.modules.reactions.service.ReactionService;
import at.campus.backend.security.UserContext;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Public controller for reading reaction counts.
 * Available to all users (authenticated and anonymous).
 */
@RestController
@RequestMapping("/api/public")
public class ReactionPublicController {

    private final ReactionService reactionService;
    private final UserContext userContext;

    public ReactionPublicController(ReactionService reactionService, UserContext userContext) {
        this.reactionService = reactionService;
        this.userContext = userContext;
    }

    /**
     * Get reaction counts for a post.
     */
    @GetMapping("/posts/{postId}/reactions")
    public ReactionCountDto getPostReactions(@PathVariable UUID postId) {
        UUID currentUserId = userContext.getUserId() != null 
            ? UUID.fromString(userContext.getUserId()) 
            : null;
        
        return reactionService.getReactionCounts(TargetType.POST, postId, currentUserId);
    }

    /**
     * Get reaction counts for a review.
     */
    @GetMapping("/reviews/{reviewId}/reactions")
    public ReactionCountDto getReviewReactions(@PathVariable UUID reviewId) {
        UUID currentUserId = userContext.getUserId() != null 
            ? UUID.fromString(userContext.getUserId()) 
            : null;
        
        return reactionService.getReactionCounts(TargetType.REVIEW, reviewId, currentUserId);
    }
}
