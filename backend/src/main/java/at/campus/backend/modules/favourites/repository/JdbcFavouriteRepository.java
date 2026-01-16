package at.campus.backend.modules.favourites.repository;

import at.campus.backend.modules.favourites.model.Favourite;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
