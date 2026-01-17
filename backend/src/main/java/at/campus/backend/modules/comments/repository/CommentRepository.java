package at.campus.backend.modules.comments.repository;

import at.campus.backend.modules.comments.model.Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Comment persistence.
 */
public interface CommentRepository {

    /**
     * Find all comments for a specific post.
     */
    List<Comment> findByPostId(UUID postId);

    /**
     * Find a comment by ID.
     */
    Optional<Comment> findById(UUID id);

    /**
     * Save a new comment.
     */
    void save(Comment comment);

    /**
     * Update an existing comment.
     */
    void update(Comment comment);

    /**
     * Delete a comment by ID.
     */
    void deleteById(UUID id);

    /**
     * Get the count of comments for a specific post.
     */
    Integer getCommentCountByPostId(UUID postId);
}
