package at.campus.backend.modules.posts.repository;

import at.campus.backend.modules.posts.model.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Post persistence.
 */
public interface PostRepository {

    /**
     * Find all posts for a specific thread.
     */
    List<Post> findByThreadId(UUID threadId);

    /**
     * Find a post by ID.
     */
    Optional<Post> findById(UUID id);

    /**
     * Save a new post.
     */
    void save(Post post);

    /**
     * Update an existing post.
     */
    void update(Post post);

    /**
     * Delete a post by ID.
     */
    void deleteById(UUID id);

    /**
     * Get the count of posts for a specific thread.
     */
    Integer getPostCountByThreadId(UUID threadId);
}
