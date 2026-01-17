package at.campus.backend.modules.reports.repository;

import at.campus.backend.modules.reports.model.Report;
import at.campus.backend.modules.reports.model.ReportStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Report persistence.
 */
public interface ReportRepository {

    /**
     * Find all reports.
     */
    List<Report> findAll();

    /**
     * Find a report by ID.
     */
    Optional<Report> findById(UUID id);

    /**
     * Find all reports with a specific status.
     */
    List<Report> findByStatus(ReportStatus status);

    /**
     * Find reports by target type.
     */
    List<Report> findByTargetType(String targetType);

    /**
     * Find reports for a specific target (e.g., a specific review or post).
     */
    List<Report> findByTargetTypeAndTargetId(String targetType, UUID targetId);

    /**
     * Find reports submitted by a specific user.
     */
    List<Report> findByUserId(UUID userId);

    /**
     * Check if a report already exists for the given user and target.
     * Used to prevent duplicate reports from the same user.
     */
    boolean existsByUserIdAndTargetTypeAndTargetId(UUID userId, String targetType, UUID targetId);

    /**
     * Save a new report.
     */
    void save(Report report);

    /**
     * Delete a report by ID.
     */
    void deleteById(UUID id);
}
