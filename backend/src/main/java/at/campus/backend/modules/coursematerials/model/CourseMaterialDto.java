package at.campus.backend.modules.coursematerials.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO exposed via REST API for course materials.
 *
 * IMPORTANT:
 * - Do NOT expose internal storageKey to clients.
 */
public record CourseMaterialDto(
        UUID id,
        UUID courseId,
        UUID uploaderId,
        String title,
        String description,
        String originalFilename,
        String contentType,
        long sizeBytes,
        LocalDateTime createdAt
) {
    public static CourseMaterialDto fromDomain(CourseMaterial m) {
        return new CourseMaterialDto(
                m.getId(),
                m.getCourseId(),
                m.getUploaderId(),
                m.getTitle(),
                m.getDescription(),
                m.getOriginalFilename(),
                m.getContentType(),
                m.getSizeBytes(),
                m.getCreatedAt()
        );
    }
}
