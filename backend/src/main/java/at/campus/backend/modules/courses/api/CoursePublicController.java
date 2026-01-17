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
                @RequestParam(required = false) Integer semester,
                @RequestParam(required = false) String language,
                @RequestParam(required = false) String title,
                @RequestParam(required = false) String studyProgramName,
            @RequestParam(required = false) String search,
            Pageable pageable
        ) {
        List<CourseDto> allCourses = service.getCourses(studyProgramId, ects)
            .stream()
            .map(CourseDto::fromDomain)
            .toList();

            // Column filters
        allCourses = allCourses.stream()
            .filter(c -> matchesFilters(c, title, studyProgramName, semester, language, ects))
            .toList();

        if (search != null && !search.isBlank()) {
            String query = search.toLowerCase();
            allCourses = allCourses.stream()
                .filter(c -> matchesCourse(c, query))
                .toList();
        }
        
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
                            case "studyProgram.name" -> comparison = compareStrings(
                                    a.studyProgram() != null ? a.studyProgram().name() : null,
                                    b.studyProgram() != null ? b.studyProgram().name() : null
                            );
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

    private boolean matchesCourse(CourseDto course, String query) {
        return containsIgnoreCase(course.title(), query)
                || containsIgnoreCase(course.description(), query)
                || containsIgnoreCase(course.language(), query)
                || containsIgnoreCase(course.kind(), query)
                || containsIgnoreCase(course.sourceUrl(), query)
                || containsIgnoreCase(course.studyProgram() != null ? course.studyProgram().name() : null, query)
                || containsIgnoreCase(course.studyProgram() != null ? course.studyProgram().mode() : null, query);
    }

    private boolean matchesFilters(CourseDto course,
                                   String title,
                                   String studyProgramName,
                                   Integer semester,
                                   String language,
                                   Integer ects) {
        if (title != null && !containsIgnoreCase(course.title(), title)) return false;
        if (studyProgramName != null && course.studyProgram() != null) {
            // Match against combined name + mode format: "Name (Mode)"
            String programDisplay = course.studyProgram().mode() != null
                ? course.studyProgram().name() + " (" + course.studyProgram().mode() + ")"
                : course.studyProgram().name();
            if (!containsIgnoreCase(programDisplay, studyProgramName)) return false;
        } else if (studyProgramName != null) {
            return false;
        }
        if (semester != null && (course.semester() == null || !course.semester().equals(semester))) return false;
        if (language != null && !containsIgnoreCase(course.language(), language)) return false;
        if (ects != null && course.ects() != ects) return false;
        return true;
    }

    private boolean containsIgnoreCase(String value, String query) {
        if (value == null || query == null) return false;
        return value.toLowerCase().contains(query.toLowerCase());
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
