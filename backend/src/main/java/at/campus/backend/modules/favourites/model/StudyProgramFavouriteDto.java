package at.campus.backend.modules.favourites.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for study program favourite responses.
 */
public class StudyProgramFavouriteDto {

    private UUID studyProgramId;
    private String studyProgramName;
    private String studyProgramDescription;
    private String degree;
    private Integer semesters;
    private Integer totalEcts;
    private LocalDateTime createdAt;

    // Getters and Setters
    public UUID getStudyProgramId() {
        return studyProgramId;
    }

    public void setStudyProgramId(UUID studyProgramId) {
        this.studyProgramId = studyProgramId;
    }

    public String getStudyProgramName() {
        return studyProgramName;
    }

    public void setStudyProgramName(String studyProgramName) {
        this.studyProgramName = studyProgramName;
    }

    public String getStudyProgramDescription() {
        return studyProgramDescription;
    }

    public void setStudyProgramDescription(String studyProgramDescription) {
        this.studyProgramDescription = studyProgramDescription;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public Integer getSemesters() {
        return semesters;
    }

    public void setSemesters(Integer semesters) {
        this.semesters = semesters;
    }

    public Integer getTotalEcts() {
        return totalEcts;
    }

    public void setTotalEcts(Integer totalEcts) {
        this.totalEcts = totalEcts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
