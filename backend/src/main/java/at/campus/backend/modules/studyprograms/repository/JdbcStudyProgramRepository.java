package at.campus.backend.modules.studyprograms.repository;

import at.campus.backend.modules.studyprograms.model.StudyProgram;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcStudyProgramRepository implements StudyProgramRepository {

    private final JdbcTemplate jdbc;

    public JdbcStudyProgramRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<StudyProgram> mapper = new RowMapper<StudyProgram>() {
        @Override
        public StudyProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
            StudyProgram p = new StudyProgram();
            p.setId(toUUID(rs.getString("id")));
            p.setName(rs.getString("name"));
            p.setDescription(rs.getString("description"));
            p.setDegree(rs.getString("degree"));
            p.setSemesters((Integer) rs.getObject("semesters"));
            p.setMode(rs.getString("mode"));
            p.setTotalEcts((Integer) rs.getObject("total_ects"));
            p.setLanguage(rs.getString("language"));
            p.setApplicationPeriod(rs.getString("application_period"));
            p.setStartDates(rs.getString("start_dates"));
            p.setSourceUrl(rs.getString("source_url"));
            Timestamp createdTs = rs.getTimestamp("created_at");
            p.setCreatedAt(createdTs != null ? createdTs.toLocalDateTime() : null);
            Timestamp updatedTs = rs.getTimestamp("updated_at");
            p.setUpdatedAt(updatedTs != null ? updatedTs.toLocalDateTime() : null);
            return p;
        }
    };

    @Override
    public List<StudyProgram> findAll() {
        String sql = "SELECT * FROM app.study_programs ORDER BY name ASC";
        return jdbc.query(sql, mapper);
    }

    @Override
    public Optional<StudyProgram> findById(UUID id) {
        String sql = "SELECT * FROM app.study_programs WHERE id = ?";
        List<StudyProgram> result = jdbc.query(sql, mapper, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<StudyProgram> findByName(String name) {
        String sql = "SELECT * FROM app.study_programs WHERE name = ?";
        List<StudyProgram> result = jdbc.query(sql, mapper, name);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public void create(StudyProgram program) {
        String sql = "INSERT INTO app.study_programs " +
                "(id, name, description, degree, semesters, mode, total_ects, language, " +
                "application_period, start_dates, source_url, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        LocalDateTime now = LocalDateTime.now();
        jdbc.update(sql,
            program.getId(),
                program.getName(),
                program.getDescription(),
                program.getDegree(),
                program.getSemesters(),
                program.getMode(),
                program.getTotalEcts(),
                program.getLanguage(),
                program.getApplicationPeriod(),
                program.getStartDates(),
                program.getSourceUrl(),
                now,
                now
        );
    }

    @Override
    public void update(StudyProgram program) {
        String sql = "UPDATE app.study_programs SET " +
                "name = ?, description = ?, degree = ?, semesters = ?, mode = ?, " +
                "total_ects = ?, language = ?, application_period = ?, start_dates = ?, " +
                "source_url = ?, updated_at = ? " +
                "WHERE id = ?";

        jdbc.update(sql,
                program.getName(),
                program.getDescription(),
                program.getDegree(),
                program.getSemesters(),
                program.getMode(),
                program.getTotalEcts(),
                program.getLanguage(),
                program.getApplicationPeriod(),
                program.getStartDates(),
                program.getSourceUrl(),
                LocalDateTime.now(),
            program.getId()
        );
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM app.study_programs WHERE id = ?";
        jdbc.update(sql, id);
    }

    private UUID toUUID(String value) {
        return value != null ? UUID.fromString(value) : null;
    }
}
