package at.campus.backend.modules.threads.api;

import at.campus.backend.modules.threads.model.ThreadDto;
import at.campus.backend.modules.threads.service.ThreadService;
import at.campus.backend.modules.posts.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Public thread endpoints (no authentication required).
 *
 * Endpoints:
 * - GET /api/public/courses/{courseId}/threads — Get threads for a course
 * - GET /api/public/threads/{threadId} — Get thread details
 */
@RestController
@RequestMapping("/api/public")
public class ThreadPublicController {

    private final ThreadService service;
    private final PostRepository postRepository;
    private final ObjectMapper objectMapper;

    public ThreadPublicController(ThreadService service, PostRepository postRepository, ObjectMapper objectMapper) {
        this.service = service;
        this.postRepository = postRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Get all threads for a course.
     */
    @GetMapping("/courses/{courseId}/threads")
    public List<ThreadDto> getThreadsByCourse(@PathVariable UUID courseId) {
        var threads = service.getThreadsByCourseId(courseId);
        System.out.println("DEBUG: Fetched " + threads.size() + " threads");
        var dtos = threads.stream()
            .map(thread -> {
                System.out.println("DEBUG: Thread ID = " + thread.getId() + ", Title = " + thread.getTitle());
                Integer postCount = postRepository.getPostCountByThreadId(thread.getId());
                return ThreadDto.fromDomain(thread, postCount);
            })
            .collect(Collectors.toList());
        System.out.println("DEBUG: DTO count = " + dtos.size());
        if (!dtos.isEmpty()) {
            System.out.println("DEBUG: First DTO ID = " + dtos.get(0).getId());
            try {
                String json = objectMapper.writeValueAsString(dtos.get(0));
                System.out.println("DEBUG: First DTO JSON = " + json);
            } catch (Exception e) {
                System.out.println("DEBUG: Failed to serialize: " + e.getMessage());
            }
        }
        return dtos;
    }

    /**
     * Get a specific thread by ID.
     */
    @GetMapping("/threads/{threadId}")
    public ThreadDto getThread(@PathVariable UUID threadId) {
        System.out.println("DEBUG: Fetching thread with ID = " + threadId);
        var thread = service.getThreadById(threadId);
        System.out.println("DEBUG: Found thread: ID = " + thread.getId() + ", Title = " + thread.getTitle());
        Integer postCount = postRepository.getPostCountByThreadId(threadId);
        return ThreadDto.fromDomain(thread, postCount);
    }
}
