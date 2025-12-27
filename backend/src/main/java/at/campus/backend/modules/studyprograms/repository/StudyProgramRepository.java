package at.campus.backend.modules.studyprograms.repository;

import at.campus.backend.modules.studyprograms.model.StudyProgram;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudyProgramRepository {
    List<StudyProgram> findAll();
    Optional<StudyProgram> findById(UUID id);
    void create(StudyProgram program);
    void update(StudyProgram program);
    void delete(UUID id);
    Optional<StudyProgram> findByName(String name);
}
