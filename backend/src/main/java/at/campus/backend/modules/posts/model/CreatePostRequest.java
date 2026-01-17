package at.campus.backend.modules.posts.model;

/**
 * CreatePostRequest - Request model for creating a post.
 */
public class CreatePostRequest {

    private String content;
    private String userName;

    // Constructors

    public CreatePostRequest() {
    }

    public CreatePostRequest(String content) {
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
