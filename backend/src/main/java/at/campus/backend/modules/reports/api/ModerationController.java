package at.campus.backend.modules.reports.api;

import at.campus.backend.modules.reports.model.*;
import at.campus.backend.modules.reports.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Moderation endpoints (moderator only).
 *
 * Endpoints:
 * - GET /api/moderation/reports — List all reports (ADMIN only)
 * - GET /api/moderation/reports?status=PENDING — Filter by status
 * - GET /api/moderation/reports/{id} — Get specific report (ADMIN only)
 * - PATCH /api/moderation/reports/{id} — Update report status (ADMIN only)
 * - DELETE /api/moderation/reports/{id} — Delete report (ADMIN only)
 *
 * Authorization:
 * - All endpoints require ADMIN role (moderator)
 */
@RestController
@RequestMapping("/api/moderation/reports")
public class ModerationController {

    private final ReportService service;

    public ModerationController(ReportService service) {
        this.service = service;
    }

    /**
     * Get all reports (moderator only).
     *
     * Query parameters:
     * - status: Optional filter by status (PENDING, RESOLVED, REJECTED)
     */
    @GetMapping
    public List<ReportDto> getAllReports(
            @RequestParam(name = "status", required = false) String statusFilter) {
        
        List<Report> reports;
        
        if (statusFilter != null && !statusFilter.isBlank()) {
            try {
                ReportStatus status = ReportStatus.valueOf(statusFilter.toUpperCase());
                reports = service.getReportsByStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + statusFilter);
            }
        } else {
            reports = service.getAllReports();
        }

        return reports.stream()
            .map(ReportDto::fromDomain)
            .collect(Collectors.toList());
    }

    /**
     * Get a specific report (moderator only).
     */
    @GetMapping("/{id}")
    public ReportDto getReportById(@PathVariable UUID id) {
        return ReportDto.fromDomain(service.getReportById(id));
    }

    /**
     * Update report status (moderator only).
     *
     * Allowed transitions:
     * - PENDING -> RESOLVED or REJECTED
     * - RESOLVED -> REJECTED or PENDING
     * - REJECTED -> PENDING
     */
    @PatchMapping("/{id}")
    public ReportDto updateReportStatus(
            @PathVariable UUID id,
            @RequestBody UpdateReportStatusRequest request) {
        
        try {
            ReportStatus newStatus = ReportStatus.valueOf(request.getStatus().toUpperCase());
            Report updated = service.updateReportStatus(id, newStatus, request.getModeratorNotes());
            return ReportDto.fromDomain(updated);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + request.getStatus());
        }
    }

    /**
     * Delete a report (moderator only).
     */
    @DeleteMapping("/{id}")
    public void deleteReport(@PathVariable UUID id) {
        service.deleteReport(id);
    }
}
