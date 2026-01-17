package at.campus.backend.modules.threads.repository;

import at.campus.backend.modules.threads.model.Thread;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Thread persistence.
 */
public interface ThreadRepository {

    /**
     * Find all threads for a specific course.
     */
    List<Thread> findByCourseId(UUID courseId);

    /**
     * Find a thread by ID.
     */
    Optional<Thread> findById(UUID id);

    /**
     * Save a new thread.
     */
    void save(Thread thread);

    /**
     * Update an existing thread.
     */
    void update(Thread thread);

    /**
     * Delete a thread by ID.
     */
    void deleteById(UUID id);
}
