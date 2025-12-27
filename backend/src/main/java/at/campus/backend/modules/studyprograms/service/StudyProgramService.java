package at.campus.backend.modules.studyprograms.service;

import at.campus.backend.modules.studyprograms.model.StudyProgram;
import at.campus.backend.modules.studyprograms.model.StudyProgramDetailDto;
import at.campus.backend.modules.studyprograms.model.ModuleDto;
import at.campus.backend.modules.studyprograms.repository.StudyProgramRepository;
import at.campus.backend.modules.studyprograms.repository.StudyProgramDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudyProgramService {

    private static final Logger log = LoggerFactory.getLogger(StudyProgramService.class);

    private final StudyProgramRepository repository;
    private final StudyProgramDetailsRepository detailsRepository;

    public StudyProgramService(StudyProgramRepository repository,
                               StudyProgramDetailsRepository detailsRepository) {
        this.repository = repository;
        this.detailsRepository = detailsRepository;
    }

    public List<StudyProgram> getAllPrograms() {
        log.debug("Fetching all study programs");
        return repository.findAll();
    }

    public StudyProgram getProgramById(UUID id) {
        log.debug("Fetching study program by id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Study program not found: " + id));
    }

    public StudyProgramDetailDto getProgramDetails(UUID id) {
        StudyProgram sp = getProgramById(id);
        StudyProgramDetailDto dto = new StudyProgramDetailDto();
        dto.id = sp.getId();
        dto.name = sp.getName();
        dto.description = sp.getDescription();
        dto.degree = sp.getDegree();
        dto.semesters = sp.getSemesters();
        dto.mode = sp.getMode();
        dto.totalEcts = sp.getTotalEcts();
        dto.language = sp.getLanguage();

        // Attach modules with nested courses
        dto.modules = detailsRepository.findModulesWithCourses(id);
        return dto;
    }

    public void createProgram(StudyProgram program) {
        log.info("Creating new study program: {}", program.getName());
        if (program.getId() == null) {
            program.setId(UUID.randomUUID());
        }
        repository.create(program);
    }

    public void updateProgram(StudyProgram program) {
        log.info("Updating study program: {}", program.getId());
        repository.update(program);
    }

    public void deleteProgram(UUID id) {
        log.info("Deleting study program: {}", id);
        repository.delete(id);
    }
}
