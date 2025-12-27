package at.campus.backend.modules.studyprograms.api;

import at.campus.backend.modules.studyprograms.model.StudyProgramDto;
import at.campus.backend.modules.studyprograms.service.StudyProgramService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/study-programs")
public class StudyProgramAdminController {

    private final StudyProgramService service;

    public StudyProgramAdminController(StudyProgramService service) {
        this.service = service;
    }

    @PostMapping
    public void create(@RequestBody StudyProgramDto dto) {
        service.createProgram(dto.toDomain());
    }

    @PutMapping("/{id}")
    public void update(@PathVariable UUID id, @RequestBody StudyProgramDto dto) {
        service.updateProgram(dto.toDomain(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteProgram(id);
    }
}
