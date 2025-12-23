package at.campus.backend.modules.courses.repository;

import at.campus.backend.modules.courses.model.Course;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * JDBC implementation of CourseRepository.
 *
 * SQL-first persistence.
 * No JPA, no Hibernate.
 */
@Repository
public class JdbcCourseRepository implements CourseRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcCourseRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ==================================================
    // ROW MAPPER
    // ==================================================

    private static final RowMapper<Course> COURSE_ROW_MAPPER = (rs, rowNum) ->
            new Course(
                    rs.getObject("id", UUID.class),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("ects"),
                    rs.getString("abbreviation"),
                    rs.getString("language")
            );

    // ==================================================
    // READ
    // ==================================================

    @Override
    public List<Course> findAll() {
        String sql = """
            SELECT
                id,
                title,
                description,
                ects,
                abbreviation,
                language
            FROM app.courses
        """;

        return jdbc.query(sql, COURSE_ROW_MAPPER);
    }

    @Override
    public Optional<Course> findById(UUID courseId) {
        String sql = """
            SELECT
                id,
                title,
                description,
                ects,
                abbreviation,
                language
            FROM app.courses
            WHERE id = :id
        """;

        return jdbc.query(
                sql,
                Map.of("id", courseId),
                COURSE_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<Course> findFiltered(UUID studyProgramId, Integer ects) {

        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT
                c.id,
                c.title,
                c.description,
                c.ects,
                c.abbreviation,
                c.language
            FROM app.courses c
            JOIN app.study_program_courses spc
              ON c.id = spc.course_id
            WHERE 1 = 1
        """);

        Map<String, Object> params = new HashMap<>();

        if (studyProgramId != null) {
            sql.append(" AND spc.study_program_id = :programId");
            params.put("programId", studyProgramId);
        }

        if (ects != null) {
            sql.append(" AND c.ects = :ects");
            params.put("ects", ects);
        }

        return jdbc.query(sql.toString(), params, COURSE_ROW_MAPPER);
    }

    // ==================================================
    // WRITE
    // ==================================================

    @Override
    public void insert(Course course) {

        String sql = """
            INSERT INTO app.courses (
                id,
                title,
                description,
                ects,
                abbreviation,
                language
            ) VALUES (
                :id,
                :title,
                :description,
                :ects,
                :abbreviation,
                :language
            )
        """;

        Map<String, Object> params = toParams(course);

        if (params.get("id") == null) {
            params.put("id", UUID.randomUUID());
        }

        jdbc.update(sql, params);
    }

    @Override
    public void update(Course course) {

        String sql = """
            UPDATE app.courses
            SET
                title = :title,
                description = :description,
                ects = :ects,
                abbreviation = :abbreviation,
                language = :language,
                updated_at = NOW()
            WHERE id = :id
        """;

        jdbc.update(sql, toParams(course));
    }

    @Override
    public boolean deleteById(UUID id) {
        String sql = "DELETE FROM app.courses WHERE id = :id";

        int rowsAffected = jdbc.update(
                sql,
                Map.of("id", id)
        );

        return rowsAffected > 0;
    }

    // ==================================================
    // PARAM MAPPING
    // ==================================================

    private Map<String, Object> toParams(Course course) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", course.getCourseId());
        params.put("title", course.getTitle());
        params.put("description", course.getDescription());
        params.put("ects", course.getEcts());
        params.put("abbreviation", course.getAbbreviation());
        params.put("language", course.getLanguage());
        return params;
    }
}
