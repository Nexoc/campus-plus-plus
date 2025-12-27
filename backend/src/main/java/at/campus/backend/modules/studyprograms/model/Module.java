package at.campus.backend.modules.studyprograms.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Module {
    private UUID id;
    private UUID studyProgramId;
    private String title;
    private Integer semester;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Module() {}

    public Module(UUID id, UUID studyProgramId, String title, Integer semester,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.studyProgramId = studyProgramId;
        this.title = title;
        this.semester = semester;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getStudyProgramId() { return studyProgramId; }
    public void setStudyProgramId(UUID studyProgramId) { this.studyProgramId = studyProgramId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
