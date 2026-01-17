package at.campus.backend.modules.coursematerials.api;

import at.campus.backend.modules.coursematerials.model.CourseMaterialDto;
import at.campus.backend.modules.coursematerials.service.CourseMaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * API for course materials.
 *
 * Security:
 * - Authentication required
 * - Roles: STUDENT, MODERATOR
 *
 * Endpoints:
 * - POST /api/courses/{courseId}/materials
 *   multipart/form-data:
 *   - file (required)
 *   - title (optional)
 *   - description (optional)
 *
 * - GET /api/courses/{courseId}/materials
 *   List all materials for a course
 */
@RestController
@RequestMapping("/api/courses")
public class CourseMaterialController {

    private final CourseMaterialService service;

    public CourseMaterialController(CourseMaterialService service) {
        this.service = service;
    }

    /**
     * Upload a new course material.
     */
    @PostMapping(value = "/{courseId}/materials", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public CourseMaterialDto upload(
            @PathVariable UUID courseId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description
    ) {
        return service.upload(courseId, file, title, description);
    }

    /**
     * List all materials for a course.
     */
    @GetMapping("/{courseId}/materials")
    public List<CourseMaterialDto> listByCourse(@PathVariable UUID courseId) {
        return service.listByCourseId(courseId);
    }
}
