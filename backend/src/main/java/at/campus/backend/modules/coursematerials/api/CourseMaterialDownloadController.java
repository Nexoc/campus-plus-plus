package at.campus.backend.modules.coursematerials.api;

import at.campus.backend.modules.coursematerials.service.CourseMaterialService;
import at.campus.backend.modules.coursematerials.service.CourseMaterialService.CourseMaterialDownload;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.UUID;

/**
 * Protected API for downloading course materials.
 *
 * Security:
 * - Authentication required
 * - Roles: STUDENT, MODERATOR
 *
 * Endpoint:
 * GET /api/materials/{id}/download
 */
@RestController
@RequestMapping("/api/materials")
public class CourseMaterialDownloadController {

    private final CourseMaterialService service;

    public CourseMaterialDownloadController(CourseMaterialService service) {
        this.service = service;
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable UUID id) {

        CourseMaterialDownload download = service.getDownload(id);

        InputStream stream = download.inputStream();
        String filename = download.originalFilename();
        String contentType = download.contentType();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\""
                )
                .contentType(MediaType.parseMediaType(contentType))
                .body(new InputStreamResource(stream));
    }
}
