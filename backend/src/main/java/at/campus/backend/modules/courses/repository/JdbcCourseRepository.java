package at.campus.backend.modules.courses.repository;

import at.campus.backend.modules.courses.model.Course;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    private static final ObjectMapper JSON = new ObjectMapper();

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
                rs.getString("language"),
                rs.getBigDecimal("sws"),
                (Integer) rs.getObject("semester"),
                rs.getString("kind"),
                rs.getString("details_html"),
                        readJson(rs, "content"),
                        readJson(rs, "learning_outcomes"),
                        readJson(rs, "teaching_method"),
                        readJson(rs, "exam_method"),
                        readJson(rs, "literature"),
                        readJson(rs, "teaching_language"),
                rs.getString("source_url")
            );

    private static Object readJson(ResultSet rs, String column) throws SQLException {
        String raw = rs.getString(column);
        if (raw == null) return null;
        try {
            return JSON.readValue(raw, Object.class);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to parse json column '" + column + "'", e);
        }
    }

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
                language,
                sws,
                semester,
                kind,
                details_html,
                content,
                learning_outcomes,
                teaching_method,
                exam_method,
                literature,
                teaching_language,
                source_url
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
                language,
                sws,
                semester,
                kind,
                details_html,
                content,
                learning_outcomes,
                teaching_method,
                exam_method,
                literature,
                teaching_language,
                source_url
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
                c.language,
                c.sws,
                c.semester,
                c.kind,
                c.details_html,
                c.content,
                c.learning_outcomes,
                c.teaching_method,
                c.exam_method,
                c.literature,
                c.teaching_language,
                c.source_url
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
                language,
                sws,
                semester,
                kind,
                details_html,
                content,
                learning_outcomes,
                teaching_method,
                exam_method,
                literature,
                teaching_language,
                source_url
            ) VALUES (
                :id,
                :title,
                :description,
                :ects,
                :language,
                :sws,
                :semester,
                :kind,
                :detailsHtml,
                CAST(:content AS jsonb),
                CAST(:learningOutcomes AS jsonb),
                CAST(:teachingMethod AS jsonb),
                CAST(:examMethod AS jsonb),
                CAST(:literature AS jsonb),
                CAST(:teachingLanguage AS jsonb),
                :sourceUrl
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
                language = :language,
                sws = :sws,
                semester = :semester,
                kind = :kind,
                details_html = :detailsHtml,
                content = CAST(:content AS jsonb),
                learning_outcomes = CAST(:learningOutcomes AS jsonb),
                teaching_method = CAST(:teachingMethod AS jsonb),
                exam_method = CAST(:examMethod AS jsonb),
                literature = CAST(:literature AS jsonb),
                teaching_language = CAST(:teachingLanguage AS jsonb),
                source_url = :sourceUrl,
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
        params.put("language", course.getLanguage());
        params.put("sws", course.getSws());
        params.put("semester", course.getSemester());
        params.put("kind", course.getKind());
        params.put("detailsHtml", course.getDetailsHtml());
        params.put("content", writeJson(course.getContent()));
        params.put("learningOutcomes", writeJson(course.getLearningOutcomes()));
        params.put("teachingMethod", writeJson(course.getTeachingMethod()));
        params.put("examMethod", writeJson(course.getExamMethod()));
        params.put("literature", writeJson(course.getLiterature()));
        params.put("teachingLanguage", writeJson(course.getTeachingLanguage()));
        params.put("sourceUrl", course.getSourceUrl());
        return params;
    }

    private static String writeJson(Object value) {
        if (value == null) return null;
        if (value instanceof String s) return s;
        try {
            return JSON.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize course field to JSON", e);
        }
    }
}
