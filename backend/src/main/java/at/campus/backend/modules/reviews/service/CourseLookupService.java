package at.campus.backend.modules.reviews.service;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CourseLookupService {

    private final JdbcTemplate jdbc;

    public CourseLookupService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String getCourseTitle(UUID courseId) {
        try {
            return jdbc.queryForObject(
                    "SELECT title FROM app.courses WHERE id = ?",
                    String.class,
                    courseId
            );
        } catch (Exception e) {
            return null; // frontend can fallback to courseId
        }
    }
}
