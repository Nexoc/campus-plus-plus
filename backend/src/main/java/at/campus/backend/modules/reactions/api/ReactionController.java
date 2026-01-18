package at.campus.backend.modules.reactions.api;

import at.campus.backend.modules.reactions.model.AddReactionRequest;
import at.campus.backend.modules.reactions.model.ReactionType;
import at.campus.backend.modules.reactions.model.TargetType;
import at.campus.backend.modules.reactions.service.ReactionService;
import at.campus.backend.security.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Protected controller for adding and removing reactions.
 * Requires authentication.
 */
@RestController
@RequestMapping("/api")
public class ReactionController {

    private final ReactionService reactionService;
    private final UserContext userContext;

    public ReactionController(ReactionService reactionService, UserContext userContext) {
        this.reactionService = reactionService;
        this.userContext = userContext;
    }

    /**
     * Add a reaction to a post.
     */
    @PostMapping("/posts/{postId}/reactions")
    @ResponseStatus(HttpStatus.CREATED)
    public void addReactionToPost(
        @PathVariable UUID postId,
        @RequestBody(required = false) AddReactionRequest request
    ) {
        UUID userId = UUID.fromString(userContext.getUserId());
        ReactionType reactionType = request != null && request.getReactionType() != null 
            ? request.getReactionType() 
            : ReactionType.LIKE;
        
        reactionService.addReaction(userId, TargetType.POST, postId, reactionType);
    }

    /**
     * Remove a reaction from a post.
     */
    @DeleteMapping("/posts/{postId}/reactions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeReactionFromPost(@PathVariable UUID postId) {
        UUID userId = UUID.fromString(userContext.getUserId());
        reactionService.removeReaction(userId, TargetType.POST, postId, ReactionType.LIKE);
    }

    /**
     * Add a reaction to a review.
     */
    @PostMapping("/reviews/{reviewId}/reactions")
    @ResponseStatus(HttpStatus.CREATED)
    public void addReactionToReview(
        @PathVariable UUID reviewId,
        @RequestBody(required = false) AddReactionRequest request
    ) {
        UUID userId = UUID.fromString(userContext.getUserId());
        ReactionType reactionType = request != null && request.getReactionType() != null 
            ? request.getReactionType() 
            : ReactionType.LIKE;
        
        reactionService.addReaction(userId, TargetType.REVIEW, reviewId, reactionType);
    }

    /**
     * Remove a reaction from a review.
     */
    @DeleteMapping("/reviews/{reviewId}/reactions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeReactionFromReview(@PathVariable UUID reviewId) {
        UUID userId = UUID.fromString(userContext.getUserId());
        reactionService.removeReaction(userId, TargetType.REVIEW, reviewId, ReactionType.LIKE);
    }
}
