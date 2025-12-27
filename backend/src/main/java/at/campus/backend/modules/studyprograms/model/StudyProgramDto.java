package at.campus.backend.modules.studyprograms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public class StudyProgramDto {
    @JsonProperty("studyProgramId")
    public UUID id;

    public String name;
    public String description;
    public String degree;
    public Integer semesters;
    public String mode;
    public Integer totalEcts;
    public String language;
    public String applicationPeriod;
    public String startDates;
    public String sourceUrl;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public StudyProgramDto() {}

    public StudyProgramDto(
            UUID id,
            String name,
            String description,
            String degree,
            Integer semesters,
            String mode,
            Integer totalEcts,
            String language,
            String applicationPeriod,
            String startDates,
            String sourceUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.degree = degree;
        this.semesters = semesters;
        this.mode = mode;
        this.totalEcts = totalEcts;
        this.language = language;
        this.applicationPeriod = applicationPeriod;
        this.startDates = startDates;
        this.sourceUrl = sourceUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static StudyProgramDto fromDomain(StudyProgram domain) {
        return new StudyProgramDto(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.getDegree(),
                domain.getSemesters(),
                domain.getMode(),
                domain.getTotalEcts(),
                domain.getLanguage(),
                domain.getApplicationPeriod(),
                domain.getStartDates(),
                domain.getSourceUrl(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    public StudyProgram toDomain() {
        return new StudyProgram(
                id,
                name,
                description,
                degree,
                semesters,
                mode,
                totalEcts,
                language,
                applicationPeriod,
                startDates,
                sourceUrl,
                createdAt,
                updatedAt
        );
    }

    public StudyProgram toDomain(UUID id) {
        this.id = id;
        return toDomain();
    }
}
