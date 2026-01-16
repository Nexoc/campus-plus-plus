package at.campus.backend.modules.courses.model;

import java.util.UUID;

/**
 * Lightweight reference to a Study Program from Course context.
 * Contains only essential fields needed for course listings.
 */
public record StudyProgramRef(
        UUID id,
        String name,
        String mode
) {
}
