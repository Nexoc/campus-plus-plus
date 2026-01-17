package at.campus.backend.modules.coursematerials.api;

import at.campus.backend.modules.coursematerials.model.CourseMaterialDto;
import at.campus.backend.modules.coursematerials.model.CourseMaterialUpdateRequest;
import at.campus.backend.modules.coursematerials.service.CourseMaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Protected API for managing course materials.
 *
 * - PUT    /api/materials/{id}
 * - DELETE /api/materials/{id}
 */
@RestController
@RequestMapping("/api/materials")
public class CourseMaterialManagementController {

    private final CourseMaterialService service;

    public CourseMaterialManagementController(CourseMaterialService service) {
        this.service = service;
    }

    @PutMapping("/{id}")
    public CourseMaterialDto update(
            @PathVariable UUID id,
            @RequestBody CourseMaterialUpdateRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
