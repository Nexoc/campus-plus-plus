package at.campus.backend.modules.reviews.repository;

import at.campus.backend.modules.reviews.model.Review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Review persistence.
 */
public interface ReviewRepository {

    /**
     * Find all reviews for a specific course.
     */
    List<Review> findByCourseId(UUID courseId);

    /**
     * Find a review by ID.
     */
    Optional<Review> findById(UUID id);

    /**
     * Find all reviews.
     */
    List<Review> findAll();

    /**
     * Save a new review.
     */
    void save(Review review);

    /**
     * Update an existing review.
     */
    void update(Review review);

    /**
     * Delete a review by ID.
     */
    void deleteById(UUID id);

    /**
     * Check if a review exists for a given user and course.
     * (For preventing duplicate reviews)
     */
    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);

    /**
     * Get the average rating for a specific course.
     * Returns null if no reviews exist.
     */
    Double getAverageRatingByCourseId(UUID courseId);

    /**
     * Get the count of reviews for a specific course.
     */
    Integer getReviewCountByCourseId(UUID courseId);
}
