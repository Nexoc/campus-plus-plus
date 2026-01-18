package at.campus.backend.modules.reactions.service;

import at.campus.backend.modules.reactions.model.*;
import at.campus.backend.modules.reactions.repository.ReactionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for managing reactions.
 */
@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;

    public ReactionService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    /**
     * Add a reaction to a target.
     */
    public void addReaction(UUID userId, TargetType targetType, UUID targetId, ReactionType reactionType) {
        Reaction reaction = new Reaction(
            UUID.randomUUID(),
            userId,
            targetType,
            targetId,
            reactionType
        );
        reactionRepository.addReaction(reaction);
    }

    /**
     * Remove a reaction from a target.
     */
    public void removeReaction(UUID userId, TargetType targetType, UUID targetId, ReactionType reactionType) {
        reactionRepository.removeReaction(userId, targetType, targetId, reactionType);
    }

    /**
     * Toggle a reaction (add if not present, remove if present).
     */
    public void toggleReaction(UUID userId, TargetType targetType, UUID targetId, ReactionType reactionType) {
        if (reactionRepository.hasUserReacted(userId, targetType, targetId, reactionType)) {
            removeReaction(userId, targetType, targetId, reactionType);
        } else {
            addReaction(userId, targetType, targetId, reactionType);
        }
    }

    /**
     * Get reaction counts for a target.
     */
    public ReactionCountDto getReactionCounts(TargetType targetType, UUID targetId, UUID currentUserId) {
        int likeCount = reactionRepository.countReactions(targetType, targetId, ReactionType.LIKE);
        boolean currentUserLiked = false;
        
        if (currentUserId != null) {
            currentUserLiked = reactionRepository.hasUserReacted(currentUserId, targetType, targetId, ReactionType.LIKE);
        }
        
        return new ReactionCountDto(likeCount, currentUserLiked);
    }
}
