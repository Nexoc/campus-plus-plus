package at.campus.backend.modules.comments.api;

import at.campus.backend.modules.comments.model.CommentDto;
import at.campus.backend.modules.comments.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Public controller for reading comments.
 * Available to all users (authenticated and anonymous).
 */
@RestController
@RequestMapping("/api/public")
public class CommentPublicController {

    private final CommentService commentService;

    public CommentPublicController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Get all comments for a post.
     */
    @GetMapping("/posts/{postId}/comments")
    public List<CommentDto> getCommentsByPostId(@PathVariable UUID postId) {
        return commentService.getCommentsByPostId(postId);
    }

    /**
     * Get a specific comment by ID.
     */
    @GetMapping("/comments/{commentId}")
    public CommentDto getCommentById(@PathVariable UUID commentId) {
        return commentService.getCommentById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }
}
