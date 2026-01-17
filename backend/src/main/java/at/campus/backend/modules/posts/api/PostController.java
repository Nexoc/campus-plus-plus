package at.campus.backend.modules.posts.api;

import at.campus.backend.modules.posts.model.CreatePostRequest;
import at.campus.backend.modules.posts.model.PostDto;
import at.campus.backend.modules.posts.model.UpdatePostRequest;
import at.campus.backend.modules.posts.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Protected controller for creating, updating, and deleting posts.
 * Requires authentication.
 */
@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Create a new post in a thread.
     */
    @PostMapping("/threads/{threadId}/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto createPost(
        @PathVariable UUID threadId,
        @RequestBody CreatePostRequest request
    ) {
        return postService.createPost(threadId, request);
    }

    /**
     * Update a post.
     */
    @PutMapping("/posts/{postId}")
    public PostDto updatePost(
        @PathVariable UUID postId,
        @RequestBody UpdatePostRequest request
    ) {
        return postService.updatePost(postId, request);
    }

    /**
     * Delete a post.
     */
    @DeleteMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable UUID postId) {
        postService.deletePost(postId);
    }
}
