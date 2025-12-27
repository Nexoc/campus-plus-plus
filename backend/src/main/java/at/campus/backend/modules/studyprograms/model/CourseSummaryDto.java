package at.campus.backend.modules.studyprograms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class CourseSummaryDto {
    @JsonProperty("courseId")
    public UUID id;
    public String title;
    public Integer ects;
    public String language;

    public CourseSummaryDto() {}

    public CourseSummaryDto(UUID id, String title, Integer ects, String language) {
        this.id = id;
        this.title = title;
        this.ects = ects;
        this.language = language;
    }
}
