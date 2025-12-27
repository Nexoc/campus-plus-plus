package at.campus.backend.modules.studyprograms.api;

import at.campus.backend.modules.studyprograms.model.StudyProgramDto;
import at.campus.backend.modules.studyprograms.service.StudyProgramService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/study-programs")
public class StudyProgramController {

    private final StudyProgramService service;

    public StudyProgramController(StudyProgramService service) {
        this.service = service;
    }

    // ---------- READ (PUBLIC) ----------

    @GetMapping
    public List<StudyProgramDto> getPrograms() {
        return service.getAllPrograms()
                .stream()
                .map(StudyProgramDto::fromDomain)
                .toList();
    }

    @GetMapping("/{id}")
    public StudyProgramDto getById(@PathVariable UUID id) {
        return StudyProgramDto.fromDomain(service.getProgramById(id));
    }

    // ---------- WRITE (ADMIN) ----------

    @PostMapping
    public void create(@RequestBody StudyProgramDto dto) {
        service.createProgram(dto.toDomain());
    }

    @PutMapping("/{id}")
    public void update(
            @PathVariable UUID id,
            @RequestBody StudyProgramDto dto
    ) {
        service.updateProgram(dto.toDomain(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteProgram(id);
    }
}
