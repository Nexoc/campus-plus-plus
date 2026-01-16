package at.campus.backend.modules.reviews.api;

import at.campus.backend.modules.reviews.model.ModerationReviewDto;
import at.campus.backend.modules.reviews.model.Review;
import at.campus.backend.modules.reviews.model.ReviewDto;
import at.campus.backend.modules.reviews.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Moderation endpoints for reviews.
 * Requires ADMIN (Moderator) role via NGINX gateway.
 *
 * Endpoints:
 * - GET /api/moderation/reviews — List all reviews with filters
 * - POST /api/moderation/reviews/{id}/flag — Flag a review
 * - POST /api/moderation/reviews/{id}/unflag — Unflag a review
 * - DELETE /api/moderation/reviews/{id} — Delete a review
 */
@RestController
@RequestMapping("/api/moderation/reviews")
public class ModerationReviewsController {

    private final ReviewService service;

    public ModerationReviewsController(ReviewService service) {
        this.service = service;
    }

    /**
     * Get all reviews (moderator only).
     *
     * Query parameters:
     * - courseId: Filter by course ID
     * - flaggedOnly: If true, show only flagged reviews
     */
    @GetMapping
    public List<ModerationReviewDto> getAllReviews(
            @RequestParam(required = false) UUID courseId,
            @RequestParam(required = false) Boolean flaggedOnly
    ) {
        List<Review> reviews = service.getAllReviews();

        if (courseId != null) {
            reviews = reviews.stream()
                    .filter(r -> r.getCourseId().equals(courseId))
                    .toList();
        }

        if (Boolean.TRUE.equals(flaggedOnly)) {
            reviews = reviews.stream()
                    .filter(Review::isModerationFlagged)
                    .toList();
        }

        return service.toModerationDtos(reviews);
    }


    /**
     * Flag a review as inappropriate (moderator only).
     */
    @PostMapping("/{id}/flag")
    public ReviewDto flagReview(
        @PathVariable UUID id,
        @RequestBody(required = false) FlagRequest request
    ) {
        String reason = request != null ? request.getReason() : null;
        Review flagged = service.flagReview(id, reason);
        return ReviewDto.fromDomain(flagged);
    }

    /**
     * Unflag a review (moderator only).
     */
    @PostMapping("/{id}/unflag")
    public ReviewDto unflagReview(@PathVariable UUID id) {
        Review unflagged = service.unflagReview(id);
        return ReviewDto.fromDomain(unflagged);
    }

    /**
     * Delete a review (moderator only).
     */
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable UUID id) {
        service.deleteReviewByModerator(id);
    }

    /**
     * Request body for flag endpoint.
     */
    public static class FlagRequest {
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
