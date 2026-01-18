package at.campus.backend.modules.reactions.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Reaction domain model.
 *
 * Represents a user's reaction (like) to a post or review.
 */
public class Reaction {

    private UUID id;
    private UUID userId;
    private TargetType targetType;
    private UUID targetId;
    private ReactionType reactionType;
    private LocalDateTime createdAt;

    // Constructors

    public Reaction() {
    }

    public Reaction(UUID id, UUID userId, TargetType targetType, UUID targetId, ReactionType reactionType) {
        this.id = id;
        this.userId = userId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reactionType = reactionType;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public void setTargetId(UUID targetId) {
        this.targetId = targetId;
    }

    public ReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
