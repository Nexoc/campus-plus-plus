package at.campus.backend.modules.reviews.api;

import at.campus.backend.modules.reviews.model.CreateReviewRequest;
import at.campus.backend.modules.reviews.model.Review;
import at.campus.backend.modules.reviews.model.ReviewDto;
import at.campus.backend.modules.reviews.model.UpdateReviewRequest;
import at.campus.backend.modules.reviews.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Protected review endpoints.
 * Requires authentication via NGINX gateway.
 *
 * Endpoints:
 * - POST /api/reviews — Create review
 * - PUT /api/reviews/{id} — Update review
 * - DELETE /api/reviews/{id} — Delete review
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    /**
     * Create a new review.
     *
     * Authorization:
     * - Only STUDENT and ADMIN roles
     * - User ID is taken from UserContext
     *
     * Validation:
     * - Rating is required (1-5)
     * - Course ID is required
     */
    @PostMapping
    public ReviewDto createReview(@RequestBody CreateReviewRequest request) {
        // Convert request to domain model
        Review review = new Review();
        review.setCourseId(request.getCourseId());
        review.setRating(request.getRating());
        review.setDifficulty(request.getDifficulty());
        review.setWorkload(request.getWorkload());
        review.setSatisfaction(request.getSatisfaction());
        review.setPriorRequirements(request.getPriorRequirements());
        review.setExamInfo(request.getExamInfo());
        review.setText(request.getText());

        // Create review via service (handles authorization)
        Review created = service.createReview(review);

        // Return DTO
        return ReviewDto.fromDomain(created);
    }

    /**
     * Update an existing review.
     *
     * Authorization:
     * - Only the author can update
     */
    @PutMapping("/{id}")
    public ReviewDto updateReview(@PathVariable UUID id, @RequestBody UpdateReviewRequest request) {
        // Convert request to domain model
        Review review = new Review();
        review.setRating(request.getRating());
        review.setDifficulty(request.getDifficulty());
        review.setWorkload(request.getWorkload());
        review.setSatisfaction(request.getSatisfaction());
        review.setPriorRequirements(request.getPriorRequirements());
        review.setExamInfo(request.getExamInfo());
        review.setText(request.getText());

        // Update via service
        Review updated = service.updateReview(id, review);

        // Return DTO
        return ReviewDto.fromDomain(updated);
    }

    /**
     * Delete a review.
     *
     * Authorization:
     * - Only the author can delete
     */
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable UUID id) {
        service.deleteReview(id);
    }
}
