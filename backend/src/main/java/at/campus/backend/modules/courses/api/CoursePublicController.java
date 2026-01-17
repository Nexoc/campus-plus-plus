package at.campus.backend.modules.courses.api;

import at.campus.backend.modules.courses.model.CourseDto;
import at.campus.backend.modules.courses.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/courses")
public class CoursePublicController {

    private final CourseService service;

    public CoursePublicController(CourseService service) {
        this.service = service;
    }

    // ---------- READ ----------

    @GetMapping
    public Page<CourseDto> getCourses(
            @RequestParam(required = false) UUID studyProgramId,
            @RequestParam(required = false) Integer ects,
            Pageable pageable
    ) {
        List<CourseDto> allCourses = service.getCourses(studyProgramId, ects)
                .stream()
                .map(CourseDto::fromDomain)
                .toList();
        
        // Apply sorting
        if (pageable.getSort().isSorted()) {
            allCourses = allCourses.stream()
                    .sorted((a, b) -> {
                        var order = pageable.getSort().iterator().next();
                        String property = order.getProperty();
                        int comparison = 0;
                        
                        switch (property) {
                            case "title" -> comparison = compareStrings(a.title(), b.title());
                            case "ects" -> comparison = compareIntegers(a.ects(), b.ects());
                            case "semester" -> comparison = compareIntegers(a.semester(), b.semester());
                            case "language" -> comparison = compareStrings(a.language(), b.language());
                            default -> comparison = 0;
                        }
                        
                        return order.isAscending() ? comparison : -comparison;
                    })
                    .toList();
        }
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allCourses.size());
        
        if (start >= allCourses.size()) {
            return new PageImpl<>(List.of(), pageable, allCourses.size());
        }
        
        return new PageImpl<>(allCourses.subList(start, end), pageable, allCourses.size());
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
    public CourseDto getById(@PathVariable UUID id) {
        return CourseDto.fromDomain(service.getCourseById(id));
    }

}
