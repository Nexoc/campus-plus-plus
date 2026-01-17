package at.campus.backend.modules.posts.model;

/**
 * UpdatePostRequest - Request model for updating a post.
 */
public class UpdatePostRequest {

    private String content;

    // Constructors

    public UpdatePostRequest() {
    }

    public UpdatePostRequest(String content) {
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
