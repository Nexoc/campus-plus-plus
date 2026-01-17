package at.campus.backend.modules.studyprograms.api;

import at.campus.backend.modules.studyprograms.model.StudyProgramDto;
import at.campus.backend.modules.studyprograms.service.StudyProgramService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
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
    public Page<StudyProgramDto> getPrograms(Pageable pageable) {
        List<StudyProgramDto> allPrograms = service.getAllPrograms()
                .stream()
                .map(StudyProgramDto::fromDomain)
                .toList();
        
        // Apply sorting
        if (pageable.getSort().isSorted()) {
            allPrograms = allPrograms.stream()
                    .sorted((a, b) -> {
                        var order = pageable.getSort().iterator().next();
                        String property = order.getProperty();
                        int comparison = 0;
                        
                        switch (property) {
                            case "name" -> comparison = compareStrings(a.name, b.name);
                            case "degree" -> comparison = compareStrings(a.degree, b.degree);
                            case "semesters" -> comparison = compareIntegers(a.semesters, b.semesters);
                            case "totalEcts" -> comparison = compareIntegers(a.totalEcts, b.totalEcts);
                            case "mode" -> comparison = compareStrings(a.mode, b.mode);
                            case "language" -> comparison = compareStrings(a.language, b.language);
                            default -> comparison = 0;
                        }
                        
                        return order.isAscending() ? comparison : -comparison;
                    })
                    .toList();
        }
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allPrograms.size());
        
        if (start >= allPrograms.size()) {
            return new PageImpl<>(List.of(), pageable, allPrograms.size());
        }
        
        List<StudyProgramDto> pageContent = allPrograms.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allPrograms.size());
    }
    
    private int compareStrings(String a, String b) {
        if (a == null && b == null) return 0;
        if (a == null) return 1;
        if (b == null) return -1;
        return a.compareToIgnoreCase(b);
    }
    
    private int compareIntegers(Integer a, Integer b) {
        if (a == null && b == null) return 0;
        if (a == null) return 1;
        if (b == null) return -1;
        return a.compareTo(b);
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
