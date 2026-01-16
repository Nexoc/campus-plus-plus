package at.campus.backend.modules.reports.service;

import at.campus.backend.modules.reports.model.Report;
import at.campus.backend.modules.reports.model.ReportStatus;
import at.campus.backend.modules.reports.repository.ReportRepository;
import at.campus.backend.security.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for Report business logic.
 *
 * Responsibilities:
 * - Moderation-only authorization
 * - Report management (create, update, list)
 * - Data validation
 *
 * Role mapping:
 * - Moderator (backend: ADMIN role)
 */
@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ReportRepository repository;
    private final UserContext userContext;

    public ReportService(ReportRepository repository, UserContext userContext) {
        this.repository = repository;
        this.userContext = userContext;
    }

    /**
     * Get all reports (moderator only).
     *
     * Authorization:
     * - Only ADMIN role (moderator)
     */
    public List<Report> getAllReports() {
        requireModerator();
        return repository.findAll();
    }

    /**
     * Get reports by status (moderator only).
     */
    public List<Report> getReportsByStatus(ReportStatus status) {
        requireModerator();
        return repository.findByStatus(status);
    }

    /**
     * Get a single report (moderator only).
     */
    public Report getReportById(UUID id) {
        requireModerator();
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
    }

    /**
     * Create a new report (any authenticated user).
     *
     * Authorization:
     * - Any authenticated user (STUDENT or ADMIN)
     *
     * Validation:
     * - Target type is required (review, post, thread)
     * - Target ID is required
     * - Reason is required and not empty
     */
    public Report createReport(Report report) {
        // 1. Check authentication
        String userId = userContext.getUserId();
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        // 2. Validate required fields
        if (report.getTargetType() == null || report.getTargetType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target type is required");
        }

        if (report.getTargetId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target ID is required");
        }

        if (report.getReason() == null || report.getReason().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reason is required");
        }

        // 3. Enforce user ownership (security)
        // User ID MUST come from UserContext, never from request
        UUID authenticatedUserId = UUID.fromString(userId);
        report.setUserId(authenticatedUserId);

        // 4. Generate ID and save
        report.setId(UUID.randomUUID());
        report.setStatus(ReportStatus.PENDING);
        report.setCreatedAt(OffsetDateTime.now());
        repository.save(report);

        log.info("Report created: {} for {} with ID {}", 
            report.getTargetType(), report.getTargetId(), report.getId());

        return report;
    }

    /**
     * Update a report status (moderator only).
     *
     * Authorization:
     * - Only ADMIN role (moderator)
     *
     * Allowed transitions:
     * - PENDING -> RESOLVED or REJECTED
     * - RESOLVED -> REJECTED or PENDING (re-open)
     * - REJECTED -> PENDING (re-open)
     */
    public Report updateReportStatus(UUID id, ReportStatus newStatus, String moderatorNotes) {
        // 1. Check authorization
        requireModerator();

        // 2. Find existing report
        Report existing = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));

        // 3. Validate status transition
        if (newStatus == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        // 4. Update status
        existing.setStatus(newStatus);
        if (newStatus != ReportStatus.PENDING) {
            existing.setResolvedAt(OffsetDateTime.now());
        }

        // 5. Add moderator notes
        if (moderatorNotes != null && !moderatorNotes.isBlank()) {
            existing.setModeratorNotes(moderatorNotes);
        }

        repository.save(existing);

        log.info("Report {} updated to status {} by moderator {}", 
            id, newStatus, userContext.getUserId());

        return existing;
    }

    /**
     * Delete a report (moderator only).
     *
     * Authorization:
     * - Only ADMIN role (moderator)
     */
    public void deleteReport(UUID id) {
        requireModerator();

        Report report = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));

        repository.deleteById(id);

        log.info("Report {} deleted by moderator {}", id, userContext.getUserId());
    }

    // ==================================================
    // AUTHORIZATION HELPERS
    // ==================================================

    /**
     * Require ADMIN role (moderator).
     * Throws 403 Forbidden if user is not a moderator.
     */
    private void requireModerator() {
        if (!userContext.hasRole("ADMIN")) {
            log.warn("Forbidden: user {} attempted moderation operation", userContext.getUserId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Only moderators can access moderation functionality");
        }
    }
}
