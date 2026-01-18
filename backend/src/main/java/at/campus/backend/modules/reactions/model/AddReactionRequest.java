package at.campus.backend.modules.reactions.model;

/**
 * Request to add a reaction.
 */
public class AddReactionRequest {

    private ReactionType reactionType = ReactionType.LIKE;

    public AddReactionRequest() {
    }

    public ReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }
}
