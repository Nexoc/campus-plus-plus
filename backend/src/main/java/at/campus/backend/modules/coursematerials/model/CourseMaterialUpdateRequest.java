package at.campus.backend.modules.coursematerials.model;

/**
 * Request body for updating course material metadata.
 */
public class CourseMaterialUpdateRequest {

    private String title;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
