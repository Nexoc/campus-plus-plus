package at.campus.backend.modules.studyprograms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StudyProgramDetailDto {
    @JsonProperty("studyProgramId")
    public UUID id;
    public String name;
    public String description;
    public String degree;
    public Integer semesters;
    public String mode;
    public Integer totalEcts;
    public String language;
    public String buildingName;
    public Double buildingLat;
    public Double buildingLon;



    public List<ModuleDto> modules = new ArrayList<>();
    public List<CampusBuildingDto> campusBuildings = new ArrayList<>();


    public StudyProgramDetailDto() {}
}
