package at.campus.backend.modules.studyprograms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModuleDto {
    @JsonProperty("moduleId")
    public UUID id;
    public String title;
    public Integer semester;
    public List<CourseSummaryDto> courses = new ArrayList<>();

    public ModuleDto() {}

    public ModuleDto(UUID id, String title, Integer semester) {
        this.id = id;
        this.title = title;
        this.semester = semester;
    }
}
