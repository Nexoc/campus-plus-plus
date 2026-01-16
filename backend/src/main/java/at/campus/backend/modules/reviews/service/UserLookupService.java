package at.campus.backend.modules.reviews.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserLookupService {

    private final JdbcTemplate jdbc;

    public UserLookupService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String getUserName(UUID userId) {
        try {
            return jdbc.queryForObject(
                    "SELECT name FROM auth.users WHERE id = ?",
                    String.class,
                    userId
            );
        } catch (Exception e) {
            return null; // frontend will show "Anonymous"
        }
    }
}
