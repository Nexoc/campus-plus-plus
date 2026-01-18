package at.campus.backend.modules.reactions.repository;

import at.campus.backend.modules.reactions.model.Reaction;
import at.campus.backend.modules.reactions.model.ReactionType;
import at.campus.backend.modules.reactions.model.TargetType;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for reactions.
 */
public interface ReactionRepository {

    /**
     * Add a reaction.
     */
    Reaction addReaction(Reaction reaction);

    /**
     * Remove a reaction.
     */
    void removeReaction(UUID userId, TargetType targetType, UUID targetId, ReactionType reactionType);

    /**
     * Find a reaction by user, target, and type.
     */
    Optional<Reaction> findReaction(UUID userId, TargetType targetType, UUID targetId, ReactionType reactionType);

    /**
     * Count reactions for a target.
     */
    int countReactions(TargetType targetType, UUID targetId, ReactionType reactionType);

    /**
     * Check if a user has reacted.
     */
    boolean hasUserReacted(UUID userId, TargetType targetType, UUID targetId, ReactionType reactionType);
}
