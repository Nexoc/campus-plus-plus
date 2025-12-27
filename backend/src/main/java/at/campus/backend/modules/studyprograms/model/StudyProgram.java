package at.campus.backend.modules.studyprograms.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class StudyProgram {
    private UUID id;
    private String name;
    private String description;
    private String degree;
    private Integer semesters;
    private String mode;
    private Integer totalEcts;
    private String language;
    private String applicationPeriod;
    private String startDates;
    private String sourceUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor
    public StudyProgram() {}

    public StudyProgram(
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

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getTotalEcts() {
        return totalEcts;
    }

    public void setTotalEcts(Integer totalEcts) {
        this.totalEcts = totalEcts;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getApplicationPeriod() {
        return applicationPeriod;
    }

    public void setApplicationPeriod(String applicationPeriod) {
        this.applicationPeriod = applicationPeriod;
    }

    public String getStartDates() {
        return startDates;
    }

    public void setStartDates(String startDates) {
        this.startDates = startDates;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
