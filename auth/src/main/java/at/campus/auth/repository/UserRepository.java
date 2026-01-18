package at.campus.auth.repository;

import at.campus.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Used by authentication process (login / JWT validation)
     */
    Optional<User> findByEmail(String email);

    /**
     * Used during registration to prevent duplicates
     */
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

}
