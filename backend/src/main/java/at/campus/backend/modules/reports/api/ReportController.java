package at.campus.backend.modules.reports.api;

import at.campus.backend.modules.reports.model.*;
import at.campus.backend.modules.reports.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Public report endpoints.
 * Only authenticated users can submit reports.
 *
 * Endpoints:
 * - POST /api/reports — Submit a report (authenticated users)
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    /**
     * Submit a new report.
     *
     * Authorization:
     * - Any authenticated user (STUDENT or MODERATOR)
     *
     * Validation:
     * - Target type is required
     * - Target ID is required
     * - Reason is required (from predefined enum)
     * - Comment is optional
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportDto createReport(@RequestBody CreateReportRequest request) {
        // Convert request to domain model
        Report report = new Report();
        report.setTargetType(request.getTargetType());
        report.setTargetId(request.getTargetId());
        report.setReason(request.getReason());
        report.setComment(request.getComment());

        // Create report via service (handles authorization)
        Report created = service.createReport(report);

        // Return DTO
        return ReportDto.fromDomain(created);
    }
}
