package at.campus.backend.modules.comments.api;

import at.campus.backend.modules.comments.model.CreateCommentRequest;
import at.campus.backend.modules.comments.model.CommentDto;
import at.campus.backend.modules.comments.model.UpdateCommentRequest;
import at.campus.backend.modules.comments.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Protected controller for creating, updating, and deleting comments.
 * Requires authentication.
 */
@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Create a new comment on a post.
     */
    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(
        @PathVariable UUID postId,
        @RequestBody CreateCommentRequest request
    ) {
        return commentService.createComment(postId, request);
    }

    /**
     * Update a comment.
     */
    @PutMapping("/comments/{commentId}")
    public CommentDto updateComment(
        @PathVariable UUID commentId,
        @RequestBody UpdateCommentRequest request
    ) {
        return commentService.updateComment(commentId, request);
    }

    /**
     * Delete a comment.
     */
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
    }
}
