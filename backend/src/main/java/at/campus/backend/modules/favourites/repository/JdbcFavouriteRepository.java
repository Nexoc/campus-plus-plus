package at.campus.backend.modules.favourites.repository;

import at.campus.backend.modules.favourites.model.Favourite;
import at.campus.backend.modules.favourites.model.StudyProgramFavourite;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * JDBC implementation of FavouriteRepository.
 *
 * SQL-first persistence.
 * No JPA, no Hibernate.
 */
@Repository
public class JdbcFavouriteRepository implements FavouriteRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcFavouriteRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ==================================================
    // ROW MAPPER
    // ==================================================

    private static final RowMapper<Favourite> FAVOURITE_ROW_MAPPER = (rs, rowNum) -> {
        Favourite favourite = new Favourite(
                rs.getObject("user_id", UUID.class),
                rs.getObject("course_id", UUID.class),
                rs.getTimestamp("created_at").toLocalDateTime()
        );

        // Enrich with course details
        favourite.setCourseTitle(rs.getString("course_title"));
        favourite.setCourseDescription(rs.getString("course_description"));
        favourite.setCourseEcts((Integer) rs.getObject("course_ects"));

        return favourite;
    };

    // ==================================================
    // READ
    // ==================================================

    @Override
    public List<Favourite> findAllByUserId(UUID userId) {
        String sql = """
            SELECT
                f.user_id,
                f.course_id,
                f.created_at,
                c.title AS course_title,
                c.description AS course_description,
                c.ects AS course_ects
            FROM app.favourites f
            INNER JOIN app.courses c ON f.course_id = c.id
            WHERE f.user_id = :userId
            ORDER BY f.created_at DESC
        """;

        return jdbc.query(
                sql,
                Map.of("userId", userId),
                FAVOURITE_ROW_MAPPER
        );
    }

    @Override
    public boolean existsByUserIdAndCourseId(UUID userId, UUID courseId) {
        String sql = """
            SELECT COUNT(*) FROM app.favourites
            WHERE user_id = :userId AND course_id = :courseId
        """;

        Integer count = jdbc.queryForObject(
                sql,
                Map.of("userId", userId, "courseId", courseId),
                Integer.class
        );

        return count != null && count > 0;
    }

    // ==================================================
    // WRITE
    // ==================================================

    @Override
    public void insert(UUID userId, UUID courseId) {
        String sql = """
            INSERT INTO app.favourites (user_id, course_id, created_at)
            VALUES (:userId, :courseId, NOW())
        """;

        jdbc.update(
                sql,
                Map.of("userId", userId, "courseId", courseId)
        );
    }

    @Override
    public boolean deleteByUserIdAndCourseId(UUID userId, UUID courseId) {
        String sql = """
            DELETE FROM app.favourites
            WHERE user_id = :userId AND course_id = :courseId
        """;

        int rows = jdbc.update(
                sql,
                Map.of("userId", userId, "courseId", courseId)
        );

        return rows > 0;
    }

    // ==================================================
    // STUDY PROGRAM FAVOURITES
    // ==================================================

    @Override
    public List<StudyProgramFavourite> findAllStudyProgramsByUserId(UUID userId) {
        String sql = """
            SELECT spf.user_id, spf.study_program_id, spf.created_at,
                   sp.name, sp.description, sp.degree, sp.semesters, sp.total_ects
            FROM app.study_program_favourites spf
            LEFT JOIN app.study_programs sp ON sp.id = spf.study_program_id
            WHERE spf.user_id = :userId
            ORDER BY spf.created_at DESC
        """;

        return jdbc.query(
                sql,
                Map.of("userId", userId),
                this::mapStudyProgramFavouriteRow
        );
    }

    @Override
    public boolean existsByUserIdAndStudyProgramId(UUID userId, UUID studyProgramId) {
        String sql = """
            SELECT COUNT(*) FROM app.study_program_favourites
            WHERE user_id = :userId AND study_program_id = :studyProgramId
        """;

        Integer count = jdbc.queryForObject(
                sql,
                Map.of("userId", userId, "studyProgramId", studyProgramId),
                Integer.class
        );

        return count != null && count > 0;
    }

    @Override
    public void insertStudyProgram(UUID userId, UUID studyProgramId) {
        String sql = """
            INSERT INTO app.study_program_favourites (user_id, study_program_id)
            VALUES (:userId, :studyProgramId)
        """;

        jdbc.update(
                sql,
                Map.of("userId", userId, "studyProgramId", studyProgramId)
        );
    }

    @Override
    public boolean deleteByUserIdAndStudyProgramId(UUID userId, UUID studyProgramId) {
        String sql = """
            DELETE FROM app.study_program_favourites
            WHERE user_id = :userId AND study_program_id = :studyProgramId
        """;

        int rows = jdbc.update(
                sql,
                Map.of("userId", userId, "studyProgramId", studyProgramId)
        );

        return rows > 0;
    }

    // ==================================================
    // ROW MAPPERS
    // ==================================================

    private StudyProgramFavourite mapStudyProgramFavouriteRow(ResultSet rs, int rowNum) throws SQLException {
        StudyProgramFavourite fav = new StudyProgramFavourite(
                UUID.fromString(rs.getString("user_id")),
                UUID.fromString(rs.getString("study_program_id")),
                rs.getTimestamp("created_at").toLocalDateTime()
        );

        // Enriched fields from study_programs table
        fav.setStudyProgramName(rs.getString("name"));
        fav.setStudyProgramDescription(rs.getString("description"));
        fav.setDegree(rs.getString("degree"));

        Integer semesters = rs.getInt("semesters");
        if (!rs.wasNull()) {
            fav.setSemesters(semesters);
        }

        Integer totalEcts = rs.getInt("total_ects");
        if (!rs.wasNull()) {
            fav.setTotalEcts(totalEcts);
        }

        return fav;
    }
}
