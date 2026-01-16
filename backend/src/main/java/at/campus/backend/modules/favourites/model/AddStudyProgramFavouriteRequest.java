package at.campus.backend.modules.favourites.model;

import java.util.UUID;

/**
 * DTO for adding a study program to favourites.
 */
public class AddStudyProgramFavouriteRequest {

    private UUID studyProgramId;

    public UUID getStudyProgramId() {
        return studyProgramId;
    }

    public void setStudyProgramId(UUID studyProgramId) {
        this.studyProgramId = studyProgramId;
    }
}
