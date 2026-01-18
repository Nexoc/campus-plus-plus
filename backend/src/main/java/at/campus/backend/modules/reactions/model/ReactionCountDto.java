package at.campus.backend.modules.reactions.model;

/**
 * DTO for reaction counts.
 */
public class ReactionCountDto {

    private int likeCount;
    private boolean currentUserLiked;

    public ReactionCountDto() {
    }

    public ReactionCountDto(int likeCount, boolean currentUserLiked) {
        this.likeCount = likeCount;
        this.currentUserLiked = currentUserLiked;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isCurrentUserLiked() {
        return currentUserLiked;
    }

    public void setCurrentUserLiked(boolean currentUserLiked) {
        this.currentUserLiked = currentUserLiked;
    }
}
