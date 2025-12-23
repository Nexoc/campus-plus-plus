package at.campus.backend.modules.courses.api;

import at.campus.backend.modules.courses.model.CourseDto;
import at.campus.backend.modules.courses.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    // ---------- READ ----------

    @GetMapping
    public List<CourseDto> getCourses(
            @RequestParam(required = false) UUID studyProgramId,
            @RequestParam(required = false) Integer ects
    ) {
        return service.getCourses(studyProgramId, ects)
                .stream()
                .map(CourseDto::fromDomain)
                .toList();
    }

    @GetMapping("/{id}")
    public CourseDto getById(@PathVariable UUID id) {
        return CourseDto.fromDomain(service.getCourseById(id));
    }

    // ---------- WRITE (ADMIN) ----------

    @PostMapping
    public void create(@RequestBody CourseDto dto) {
        service.createCourse(dto.toDomain());
    }

    @PutMapping("/{id}")
    public void update(
            @PathVariable UUID id,
            @RequestBody CourseDto dto
    ) {
        service.updateCourse(dto.toDomain(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteCourse(id);
    }
}
