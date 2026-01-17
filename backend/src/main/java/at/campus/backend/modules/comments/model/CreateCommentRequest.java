package at.campus.backend.modules.comments.model;

/**
 * CreateCommentRequest - Request model for creating a comment.
 */
public class CreateCommentRequest {

    private String content;
    private String userName;

    // Constructors

    public CreateCommentRequest() {
    }

    public CreateCommentRequest(String content) {
        this.content = content;
    }

    // Getters and Setters

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
