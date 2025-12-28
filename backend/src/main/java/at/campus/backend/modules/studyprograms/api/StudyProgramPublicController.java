package at.campus.backend.modules.studyprograms.api;

import at.campus.backend.modules.studyprograms.model.StudyProgramDto;
import at.campus.backend.modules.studyprograms.service.StudyProgramService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/study-programs")
public class StudyProgramPublicController {

    private final StudyProgramService service;

    public StudyProgramPublicController(StudyProgramService service) {
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

    @GetMapping("/{id}/details")
    public at.campus.backend.modules.studyprograms.model.StudyProgramDetailDto getDetails(@PathVariable UUID id) {
        return service.getProgramDetails(id);
    }
}
