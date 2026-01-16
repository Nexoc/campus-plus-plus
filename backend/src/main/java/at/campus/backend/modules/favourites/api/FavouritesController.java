package at.campus.backend.modules.favourites.api;

import at.campus.backend.modules.favourites.model.AddFavouriteRequest;
import at.campus.backend.modules.favourites.model.AddStudyProgramFavouriteRequest;
import at.campus.backend.modules.favourites.model.Favourite;
import at.campus.backend.modules.favourites.model.FavouriteDto;
import at.campus.backend.modules.favourites.model.StudyProgramFavourite;
import at.campus.backend.modules.favourites.model.StudyProgramFavouriteDto;
import at.campus.backend.modules.favourites.service.FavouriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for favourites management.
 *
 * Endpoints:
 * - GET    /api/favourites          — List user's favourites
 * - POST   /api/favourites          — Add course to favourites
 * - DELETE /api/favourites/{courseId} — Remove course from favourites
 *
 * Access rules:
 * - All endpoints require authentication
 * - Users can only manage their own favourites
 * - user_id is derived from UserContext (NOT from request body)
 */
@RestController
@RequestMapping("/api/favourites")
public class FavouritesController {

    private static final Logger log =
            LoggerFactory.getLogger(FavouritesController.class);

    private final FavouriteService service;

    public FavouritesController(FavouriteService service) {
        this.service = service;
    }

    /**
     * GET /api/favourites
     *
     * List all favourites for the authenticated user.
     *
     * Response:
     * - 200 OK with list of favourites (empty array if none)
     * - 403 Forbidden if user is not authenticated
     */
    @GetMapping
    public List<FavouriteDto> getUserFavourites() {
        log.debug("GET /api/favourites");

        List<Favourite> favourites = service.getUserFavourites();

        return favourites.stream()
                .map(FavouriteDto::fromDomain)
                .toList();
    }

    /**
     * POST /api/favourites
     *
     * Add a course to the authenticated user's favourites.
     *
     * Request body:
     * {
     *   "courseId": "uuid"
     * }
     *
     * Response:
     * - 201 Created if successfully added
     * - 400 Bad Request if course is already in favourites
     * - 403 Forbidden if user is not authenticated
     * - 404 Not Found if course does not exist
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addFavourite(@RequestBody AddFavouriteRequest request) {
        log.debug("POST /api/favourites (courseId={})", request.courseId());

        service.addFavourite(request.courseId());
    }

    /**
     * DELETE /api/favourites/{courseId}
     *
     * Remove a course from the authenticated user's favourites.
     *
     * Response:
     * - 204 No Content if successfully removed or if favourite did not exist
     * - 403 Forbidden if user is not authenticated
     */
    @DeleteMapping("/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavourite(@PathVariable UUID courseId) {
        log.debug("DELETE /api/favourites/{}", courseId);

        service.removeFavourite(courseId);
    }

    // ==================================================
    // STUDY PROGRAM FAVOURITES
    // ==================================================

    /**
     * GET /api/favourites/study-programs
     *
     * List all study program favourites for the authenticated user.
     *
     * Response:
     * - 200 OK with list of study program favourites (empty array if none)
     * - 403 Forbidden if user is not authenticated
     */
    @GetMapping("/study-programs")
    public List<StudyProgramFavouriteDto> getStudyProgramFavourites() {
        log.debug("GET /api/favourites/study-programs");

        List<StudyProgramFavourite> favourites = service.getAllStudyProgramFavourites();

        return favourites.stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * POST /api/favourites/study-programs
     *
     * Add a study program to the authenticated user's favourites.
     *
     * Request body:
     * {
     *   "studyProgramId": "uuid"
     * }
     *
     * Response:
     * - 201 Created if successfully added
     * - 400 Bad Request if study program is already in favourites
     * - 403 Forbidden if user is not authenticated
     * - 404 Not Found if study program does not exist
     */
    @PostMapping("/study-programs")
    @ResponseStatus(HttpStatus.CREATED)
    public void addStudyProgramFavourite(@RequestBody AddStudyProgramFavouriteRequest request) {
        log.debug("POST /api/favourites/study-programs (studyProgramId={})", request.getStudyProgramId());

        service.addStudyProgramFavourite(request.getStudyProgramId());
    }

    /**
     * DELETE /api/favourites/study-programs/{studyProgramId}
     *
     * Remove a study program from the authenticated user's favourites.
     *
     * Response:
     * - 204 No Content if successfully removed or if favourite did not exist
     * - 403 Forbidden if user is not authenticated
     */
    @DeleteMapping("/study-programs/{studyProgramId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeStudyProgramFavourite(@PathVariable UUID studyProgramId) {
        log.debug("DELETE /api/favourites/study-programs/{}", studyProgramId);

        service.removeStudyProgramFavourite(studyProgramId);
    }

    // ==================================================
    // DTO MAPPING
    // ==================================================

    private StudyProgramFavouriteDto toDto(StudyProgramFavourite fav) {
        StudyProgramFavouriteDto dto = new StudyProgramFavouriteDto();
        dto.setStudyProgramId(fav.getStudyProgramId());
        dto.setStudyProgramName(fav.getStudyProgramName());
        dto.setStudyProgramDescription(fav.getStudyProgramDescription());
        dto.setDegree(fav.getDegree());
        dto.setSemesters(fav.getSemesters());
        dto.setTotalEcts(fav.getTotalEcts());
        dto.setCreatedAt(fav.getCreatedAt());
        return dto;
    }
}
