package at.campus.backend.modules.courses.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record CourseSummaryDto(
        @JsonProperty("courseId") UUID courseId,
        String title,
        Integer ects,
        String language
) {
}