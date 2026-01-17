package at.campus.backend.modules.threads.model;

/**
 * UpdateThreadRequest - Request model for updating a thread.
 */
public class UpdateThreadRequest {

    private String title;
    private String content;

    // Constructors
    public UpdateThreadRequest() {
    }

    public UpdateThreadRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getters and Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
