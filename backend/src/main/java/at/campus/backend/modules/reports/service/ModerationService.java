package at.campus.backend.modules.reports.service;

import at.campus.backend.modules.reports.model.*;
import at.campus.backend.modules.reports.repository.ReportRepository;
import at.campus.backend.modules.reviews.model.Review;
import at.campus.backend.modules.reviews.repository.ReviewRepository;
import at.campus.backend.security.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for moderation operations.
 * Handles report resolution and review moderation actions.
 *
 * FR-M-16: Provide a moderator view that lists all open reports
 * FR-M-17: Allow Moderators to change the status of a reported review
 */
@Service
public class ModerationService {

    private static final Logger log = LoggerFactory.getLogger(ModerationService.class);

    private final ReportRepository reportRepository;
    private final ReviewRepository reviewRepository;
    private final UserContext userContext;
    private final JdbcTemplate jdbc;

    public ModerationService(ReportRepository reportRepository, 
                           ReviewRepository reviewRepository,
                           UserContext userContext,
                           JdbcTemplate jdbc) {
        this.reportRepository = reportRepository;
        this.reviewRepository = reviewRepository;
        this.userContext = userContext;
        this.jdbc = jdbc;
    }

    /**
     * List reports by status with review summaries.
     * FR-M-16: Provide a moderator view that lists all open reports.
     *
     * @param status The report status to filter by
     * @return List of reports with review summaries
     */
    public List<ModerationReportDto> listReportsByStatus(ReportStatus status) {
        List<Report> reports = reportRepository.findByStatus(status);
        
        return reports.stream()
            .map(report -> {
                Review review = null;
                String userName = null;
                String courseName = null;
                String reviewerNickname = null;
                
                // Fetch reporter nickname
                if (report.getUserId() != null) {
                    try {
                        userName = jdbc.queryForObject(
                            "SELECT nickname FROM public.users WHERE id = ?",
                            String.class,
                            report.getUserId()
                        );
                        if (userName == null || userName.trim().isEmpty()) {
                            userName = "Anonymous";
                        }
                    } catch (Exception e) {
                        log.warn("Failed to fetch user name for report {}: {}", report.getId(), e.getMessage());
                        userName = "Anonymous";
                    }
                }
                
                // Fetch review and course name
                if ("REVIEW".equals(report.getTargetType())) {
                    review = reviewRepository.findById(report.getTargetId()).orElse(null);
                    
                    if (review != null && review.getCourseId() != null) {
                        try {
                            courseName = jdbc.queryForObject(
                                "SELECT title FROM app.courses WHERE id = ?",
                                String.class,
                                review.getCourseId()
                            );
                        } catch (Exception e) {
                            log.warn("Failed to fetch course name for review {}: {}", review.getId(), e.getMessage());
                            courseName = "Unknown Course";
                        }
                    }
                    
                    // Fetch reviewer nickname
                    if (review != null && review.getUserId() != null) {
                        try {
                            reviewerNickname = jdbc.queryForObject(
                                "SELECT nickname FROM public.users WHERE id = ?",
                                String.class,
                                review.getUserId()
                            );
                            if (reviewerNickname == null || reviewerNickname.trim().isEmpty()) {
                                reviewerNickname = "Anonymous";
                            }
                        } catch (Exception e) {
                            log.warn("Failed to fetch reviewer nickname for review {}: {}", review.getId(), e.getMessage());
                            reviewerNickname = "Anonymous";
                        }
                    }
                }
                
                return new ModerationReportDto(report, review, userName, courseName, reviewerNickname);
            })
            .collect(Collectors.toList());
    }

    /**
     * Resolve a report by applying a moderation action to the target review.
     * FR-M-17: Allow Moderators to change the status of a reported review.
     *
     * Actions:
     * - KEEP_VISIBLE: Review remains unchanged, report marked as REJECTED
     * - HIDE: Review is hidden (moderationFlagged = true), report marked as RESOLVED
     * - DELETE: Review is permanently removed, report marked as RESOLVED
     *
     * @param reportId The ID of the report to resolve
     * @param request The moderation decision
     */
    @Transactional
    public void resolveReport(UUID reportId, ResolveReportRequest request) {
        // 1. Validate report exists and is PENDING
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Report not found"));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "Report is already resolved. Current status: " + report.getStatus());
        }

        // 2. Validate target type
        if (!"REVIEW".equals(report.getTargetType())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "Only REVIEW reports can be resolved currently");
        }

        // 3. Find the target review
        Review review = reviewRepository.findById(report.getTargetId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Target review not found"));

        // 4. Apply moderation action
        ModerationAction action = request.getAction();
        switch (action) {
            case KEEP_VISIBLE:
                // No action on review, mark report as REJECTED
                report.setStatus(ReportStatus.REJECTED);
                log.info("Report {} rejected - review {} kept visible by moderator {}", 
                    reportId, review.getId(), userContext.getUserId());
                break;

            case EDIT:
                // Moderator edits the review content to fix issues
                if (request.getEditedReviewText() == null || request.getEditedReviewText().isBlank()) {
                    throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, 
                        "Edited review text is required for EDIT action");
                }
                review.setText(request.getEditedReviewText());
                reviewRepository.update(review);
                report.setStatus(ReportStatus.EDITED);
                log.info("Report {} edited - review {} content modified by moderator {}", 
                    reportId, review.getId(), userContext.getUserId());
                break;

            case DELETE:
                // Permanently delete the review
                reviewRepository.deleteById(review.getId());
                report.setStatus(ReportStatus.RESOLVED);
                log.info("Report {} resolved - review {} deleted by moderator {}", 
                    reportId, review.getId(), userContext.getUserId());
                break;

            default:
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid moderation action: " + action);
        }

        // 5. Update report with resolution details
        report.setResolvedAt(OffsetDateTime.now());
        if (request.getModeratorNotes() != null && !request.getModeratorNotes().isBlank()) {
            report.setModeratorNotes(request.getModeratorNotes());
        }
        reportRepository.save(report);

        log.info("Report {} resolved with action {} by moderator {}", 
            reportId, action, userContext.getUserId());
    }
}
