package at.campus.backend.modules.reviews.api;

import at.campus.backend.modules.reviews.model.ReviewDto;
import at.campus.backend.modules.reviews.model.ReviewSummary;
import at.campus.backend.modules.reviews.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Public review endpoints.
 * No authentication required.
 *
 * Endpoints:
 * - GET /api/public/reviews — Get all reviews
 * - GET /api/public/reviews/{id} — Get review by ID
 * - GET /api/public/courses/{courseId}/reviews — Get reviews for a course
 */
@RestController
@RequestMapping("/api/public")
public class ReviewPublicController {

    private final ReviewService service;

    public ReviewPublicController(ReviewService service) {
        this.service = service;
    }

    /**
     * Get all reviews.
     */
    @GetMapping("/reviews")
    public List<ReviewDto> getAllReviews() {
        return service.getAllReviews().stream()
            .map(ReviewDto::fromDomain)
            .collect(Collectors.toList());
    }

    /**
     * Get a single review by ID.
     */
    @GetMapping("/reviews/{id}")
    public ReviewDto getReviewById(@PathVariable UUID id) {
        return ReviewDto.fromDomain(service.getReviewById(id));
    }

    /**
     * Get all reviews for a specific course.
     */
    @GetMapping("/courses/{courseId}/reviews")
    public List<ReviewDto> getReviewsByCourse(@PathVariable UUID courseId) {
        return service.getReviewsByCourse(courseId).stream()
            .map(ReviewDto::fromDomain)
            .collect(Collectors.toList());
    }

    /**
     * Get review summary (average rating and count) for a specific course.
     */
    @GetMapping("/courses/{courseId}/reviews/summary")
    public ReviewSummary getReviewSummary(@PathVariable UUID courseId) {
        return service.getReviewSummary(courseId);
    }
}
