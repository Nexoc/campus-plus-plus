package at.campus.backend.modules.threads.service;

import at.campus.backend.modules.threads.model.Thread;
import at.campus.backend.modules.threads.model.UpdateThreadRequest;
import at.campus.backend.modules.threads.repository.ThreadRepository;
import at.campus.backend.security.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * ThreadService handles business logic for threads.
 */
@Service
public class ThreadService {

    private final ThreadRepository threadRepository;
    private final UserContext userContext;

    public ThreadService(ThreadRepository threadRepository, UserContext userContext) {
        this.threadRepository = threadRepository;
        this.userContext = userContext;
    }

    /**
     * Get all threads for a course (public operation).
     */
    public List<Thread> getThreadsByCourseId(UUID courseId) {
        return threadRepository.findByCourseId(courseId);
    }

    /**
     * Get a specific thread by ID (public operation).
     */
    public Thread getThreadById(UUID threadId) {
        return threadRepository.findById(threadId)
            .orElseThrow(() -> new RuntimeException("Thread not found: " + threadId));
    }

    /**
     * Create a new thread (requires authentication).
     * Authorization: Any authenticated user can create a thread.
     */
    public Thread createThread(UUID courseId, String title, String content, String userName) {
        if (userContext.getUserId() == null) {
            throw new RuntimeException("Authentication required to create a thread");
        }
        if (title == null || title.isBlank()) {
            throw new RuntimeException("Thread title is required");
        }

        Thread thread = new Thread();
        thread.setId(UUID.randomUUID());
        thread.setCourseId(courseId);
        thread.setTitle(title);
        thread.setContent(content);
        thread.setCreatedBy(UUID.fromString(userContext.getUserId()));
        thread.setCreatedByName(userName);

        threadRepository.save(thread);
        
        // Fetch the saved thread to get the timestamp
        return threadRepository.findById(thread.getId()).orElse(thread);
    }

    /**
     * Update a thread (author or moderator only).
     */
    public Thread updateThread(UUID threadId, UpdateThreadRequest request) {
        if (userContext.getUserId() == null) {
            throw new SecurityException("Only authenticated users can update threads");
        }

        Thread thread = threadRepository.findById(threadId)
            .orElseThrow(() -> new RuntimeException("Thread not found: " + threadId));

        // Check if user is the author or a moderator
        boolean isAuthor = thread.getCreatedBy().toString().equals(userContext.getUserId());
        boolean isModerator = userContext.hasRole("MODERATOR");

        if (!isAuthor && !isModerator) {
            throw new SecurityException("Only the author or a moderator can update this thread");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            thread.setTitle(request.getTitle());
        }
        thread.setContent(request.getContent());

        threadRepository.update(thread);
        
        // Fetch the updated thread
        return threadRepository.findById(threadId).orElse(thread);
    }

    /**
     * Delete a thread (author or moderator only).
     */
    public void deleteThread(UUID threadId) {
        if (userContext.getUserId() == null) {
            throw new SecurityException("Only authenticated users can delete threads");
        }

        Thread thread = threadRepository.findById(threadId)
            .orElseThrow(() -> new RuntimeException("Thread not found: " + threadId));

        // Check if user is the author or a moderator
        boolean isAuthor = thread.getCreatedBy().toString().equals(userContext.getUserId());
        boolean isModerator = userContext.hasRole("MODERATOR");

        if (!isAuthor && !isModerator) {
            throw new SecurityException("Only the author or a moderator can delete this thread");
        }

        threadRepository.deleteById(threadId);
    }
}
