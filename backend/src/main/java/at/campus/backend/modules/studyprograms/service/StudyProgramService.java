package at.campus.backend.modules.studyprograms.service;

import at.campus.backend.modules.studyprograms.model.StudyProgram;
import at.campus.backend.modules.studyprograms.repository.StudyProgramRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudyProgramService {

    private static final Logger log = LoggerFactory.getLogger(StudyProgramService.class);

    private final StudyProgramRepository repository;

    public StudyProgramService(StudyProgramRepository repository) {
        this.repository = repository;
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
