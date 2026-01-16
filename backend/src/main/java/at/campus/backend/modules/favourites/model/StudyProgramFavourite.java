package at.campus.backend.modules.favourites.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain entity representing a user's favourite study program.
 * Includes enriched study program details from JOIN queries.
 */
public class StudyProgramFavourite {

    private final UUID userId;
    private final UUID studyProgramId;
    private final LocalDateTime createdAt;

    // Enriched fields from study_programs table
    private String studyProgramName;
    private String studyProgramDescription;
    private String degree;
    private Integer semesters;
    private Integer totalEcts;

    public StudyProgramFavourite(UUID userId, UUID studyProgramId, LocalDateTime createdAt) {
        this.userId = userId;
        this.studyProgramId = studyProgramId;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getUserId() {
        return userId;
    }

    public UUID getStudyProgramId() {
        return studyProgramId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getStudyProgramName() {
        return studyProgramName;
    }

    public String getStudyProgramDescription() {
        return studyProgramDescription;
    }

    public String getDegree() {
        return degree;
    }

    public Integer getSemesters() {
        return semesters;
    }

    public Integer getTotalEcts() {
        return totalEcts;
    }

    // Setters for enrichment
    public void setStudyProgramName(String studyProgramName) {
        this.studyProgramName = studyProgramName;
    }

    public void setStudyProgramDescription(String studyProgramDescription) {
        this.studyProgramDescription = studyProgramDescription;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setSemesters(Integer semesters) {
        this.semesters = semesters;
    }

    public void setTotalEcts(Integer totalEcts) {
        this.totalEcts = totalEcts;
    }
}
