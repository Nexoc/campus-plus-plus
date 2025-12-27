package at.campus.backend.modules.studyprograms.repository;

import at.campus.backend.modules.studyprograms.model.Module;
import at.campus.backend.modules.studyprograms.model.ModuleDto;
import at.campus.backend.modules.studyprograms.model.CourseSummaryDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class StudyProgramDetailsRepository {

    private final JdbcTemplate jdbc;

    public StudyProgramDetailsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Module> moduleMapper = new RowMapper<Module>() {
        @Override
        public Module mapRow(ResultSet rs, int rowNum) throws SQLException {
            Module m = new Module();
            m.setId(UUID.fromString(rs.getString("id")));
            m.setStudyProgramId(UUID.fromString(rs.getString("study_program_id")));
            m.setTitle(rs.getString("title"));
            m.setSemester((Integer) rs.getObject("semester"));
            Timestamp c = rs.getTimestamp("created_at");
            Timestamp u = rs.getTimestamp("updated_at");
            m.setCreatedAt(c != null ? c.toLocalDateTime() : null);
            m.setUpdatedAt(u != null ? u.toLocalDateTime() : null);
            return m;
        }
    };

    public List<ModuleDto> findModulesWithCourses(UUID studyProgramId) {
        // Fetch modules
        String modSql = "SELECT id, study_program_id, title, semester, created_at, updated_at FROM app.modules WHERE study_program_id = ? ORDER BY semester NULLS LAST, title";
        List<Module> modules = jdbc.query(modSql, moduleMapper, studyProgramId);
        if (modules.isEmpty()) return Collections.emptyList();

        // Fetch courses for all modules in one go
        String placeholders = modules.stream().map(m -> "?").collect(Collectors.joining(","));
        String courseSql = "SELECT c.id, c.title, c.ects, c.language, c.module_id FROM app.courses c WHERE c.module_id IN (" + placeholders + ") ORDER BY c.title";
        Object[] args = modules.stream().map(Module::getId).toArray(Object[]::new);

        Map<UUID, List<CourseSummaryDto>> coursesByModule = new HashMap<>();
        jdbc.query(courseSql, args, rs -> {
            UUID courseId = UUID.fromString(rs.getString("id"));
            String title = rs.getString("title");
            Integer ects = (Integer) rs.getObject("ects");
            String language = rs.getString("language");
            UUID moduleId = UUID.fromString(rs.getString("module_id"));

            coursesByModule.computeIfAbsent(moduleId, k -> new ArrayList<>())
                        .add(new CourseSummaryDto(courseId, title, ects, language));
        });

        // Build DTOs
        List<ModuleDto> result = new ArrayList<>();
        for (Module m : modules) {
            ModuleDto dto = new ModuleDto(m.getId(), m.getTitle(), m.getSemester());
            dto.courses = coursesByModule.getOrDefault(m.getId(), Collections.emptyList());
            result.add(dto);
        }
        return result;
    }
}
