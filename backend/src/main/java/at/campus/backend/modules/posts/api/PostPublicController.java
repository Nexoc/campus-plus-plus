package at.campus.backend.modules.posts.api;

import at.campus.backend.modules.posts.model.PostDto;
import at.campus.backend.modules.posts.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Public controller for reading posts.
 * Available to all users (authenticated and anonymous).
 */
@RestController
@RequestMapping("/api/public")
public class PostPublicController {

    private final PostService postService;

    public PostPublicController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Get all posts for a thread.
     */
    @GetMapping("/threads/{threadId}/posts")
    public List<PostDto> getPostsByThreadId(@PathVariable UUID threadId) {
        return postService.getPostsByThreadId(threadId);
    }

    /**
     * Get a specific post by ID.
     */
    @GetMapping("/posts/{postId}")
    public PostDto getPostById(@PathVariable UUID postId) {
        return postService.getPostById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }
}
