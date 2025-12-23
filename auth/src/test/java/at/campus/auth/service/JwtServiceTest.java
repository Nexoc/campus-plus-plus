package at.campus.auth.service;

import at.campus.auth.model.User;
import at.campus.auth.model.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        // Base64-encoded secret (sufficient length for HMAC)
        String secret = "dGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQ=";
        long expirationMinutes = 10;

        jwtService = new JwtService(secret, expirationMinutes);

        // Create real User instance with role
        user = new User(
                "test@test.com",
                "hashedPassword",
                UserRole.STUDENT
        );
    }

    @Test
    void generateToken_shouldProduceValidJwt() {
        // WHEN
        String token = jwtService.generateToken(user);

        // THEN
        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void extractUsername_shouldReturnEmail() {
        // GIVEN
        String token = jwtService.generateToken(user);

        // WHEN
        String username = jwtService.extractUsername(token);

        // THEN
        assertEquals("test@test.com", username);
    }

    @Test
    void extractAuthorities_shouldReturnRoleFromUser() {
        // GIVEN
        String token = jwtService.generateToken(user);

        // WHEN
        List<String> authorities = jwtService.extractAuthorities(token);

        // THEN
        assertEquals(1, authorities.size());
        assertEquals("ROLE_STUDENT", authorities.get(0));
    }

    @Test
    void extractTokenVersion_shouldReturnDefaultVersion() {
        // GIVEN
        String token = jwtService.generateToken(user);

        // WHEN
        int version = jwtService.extractTokenVersion(token);

        // THEN
        assertEquals(0, version);
    }

    @Test
    void isTokenValid_shouldReturnFalse_forInvalidToken() {
        // GIVEN
        String invalidToken = "invalid.jwt.token";

        // WHEN / THEN
        assertFalse(jwtService.isTokenValid(invalidToken));
    }

    @Test
    void isTokenValid_shouldReturnFalse_forExpiredToken() throws InterruptedException {
        // GIVEN: token with immediate expiration
        JwtService shortLivedJwtService =
                new JwtService(
                        "dGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQ=",
                        0
                );

        String token = shortLivedJwtService.generateToken(user);

        // Ensure expiration timestamp is in the past
        Thread.sleep(5);

        // THEN
        assertFalse(shortLivedJwtService.isTokenValid(token));
    }

    @Test
    void extractAuthorities_shouldReturnEmptyList_whenNoAuthoritiesClaim() {
        // GIVEN: manually build token without authorities
        String token = Jwts.builder()
                .setSubject("test@test.com")
                .signWith(Keys.hmacShaKeyFor(
                        Base64.getDecoder().decode("dGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQ=")
                ))
                .compact();

        // WHEN
        List<String> authorities = jwtService.extractAuthorities(token);

        // THEN
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void extractTokenVersion_shouldReturnZero_whenClaimMissing() {
        // GIVEN
        String token = Jwts.builder()
                .setSubject("test@test.com")
                .signWith(Keys.hmacShaKeyFor(
                        Base64.getDecoder().decode("dGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQ=")
                ))
                .compact();

        // WHEN
        int version = jwtService.extractTokenVersion(token);

        // THEN
        assertEquals(0, version);
    }


}
