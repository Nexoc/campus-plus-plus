package at.campus.backend.modules.reviews.api;

import at.campus.backend.modules.reviews.model.ReviewDto;
import at.campus.backend.modules.reviews.model.ReviewSortOption;
import at.campus.backend.modules.reviews.model.ReviewSummary;
import at.campus.backend.modules.reviews.service.ReviewService;
import at.campus.backend.modules.reviews.service.UserLookupService;
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
    private final UserLookupService userLookupService;

    public ReviewPublicController(ReviewService service, UserLookupService userLookupService) {
        this.service = service;
        this.userLookupService = userLookupService;
    }

    /**
     * Get all reviews.
     */
    @GetMapping("/reviews")
    public List<ReviewDto> getAllReviews() {
        return service.getAllReviews().stream()
            .map(review -> {
                ReviewDto dto = ReviewDto.fromDomain(review);
                String nickname = userLookupService.getUserName(review.getUserId());
                dto.setUserName(nickname != null && !nickname.isEmpty() ? nickname : "Anonymous");
                return dto;
            })
            .collect(Collectors.toList());
    }

    /**
     * Get a single review by ID.
     */
    @GetMapping("/reviews/{id}")
    public ReviewDto getReviewById(@PathVariable UUID id) {
        var review = service.getReviewById(id);
        ReviewDto dto = ReviewDto.fromDomain(review);
        String nickname = userLookupService.getUserName(review.getUserId());
        dto.setUserName(nickname != null && !nickname.isEmpty() ? nickname : "Anonymous");
        return dto;
    }

    /**
     * Get all reviews for a specific course.
     * 
     * @param courseId The course ID
     * @param sort Optional sort parameter (newest, oldest, highest_rating, lowest_rating). Default: newest
     */
    @GetMapping("/courses/{courseId}/reviews")
    public List<ReviewDto> getReviewsByCourse(
            @PathVariable UUID courseId,
            @RequestParam(required = false) String sort
    ) {
        ReviewSortOption sortOption = ReviewSortOption.fromString(sort);
        return service.getReviewsByCourse(courseId, sortOption).stream()
            .map(review -> {
                ReviewDto dto = ReviewDto.fromDomain(review);
                // Fetch and set user nickname
                String nickname = userLookupService.getUserName(review.getUserId());
                dto.setUserName(nickname != null && !nickname.isEmpty() ? nickname : "Anonymous");
                return dto;
            })
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
