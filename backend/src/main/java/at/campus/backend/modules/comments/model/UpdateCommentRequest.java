package at.campus.backend.modules.comments.model;

/**
 * UpdateCommentRequest - Request model for updating a comment.
 */
public class UpdateCommentRequest {

    private String content;

    // Constructors

    public UpdateCommentRequest() {
    }

    public UpdateCommentRequest(String content) {
        this.content = content;
    }

    // Getters and Setters

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
