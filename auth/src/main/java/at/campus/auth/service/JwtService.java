package at.campus.auth.service;

import at.campus.auth.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JwtService
 *
 * Responsible for all JWT-related operations:
 * - generating tokens
 * - validating tokens
 * - extracting claims
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private static final String CLAIM_AUTHORITIES = "authorities";
    private static final String CLAIM_TOKEN_VERSION = "ver";

    private final SecretKey signingKey;
    private final long expirationSeconds;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-minutes}") long expirationMinutes
    ) {
        // Decode base64 secret (recommended for HMAC keys)
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException ex) {
            // fallback: treat as plain text
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationSeconds = expirationMinutes * 60;

        log.info("JwtService initialized with expiration={} seconds", this.expirationSeconds);
    }

    /* =========================
       Token generation
       ========================= */

    /**
     * Generates a JWT token for an authenticated user.
     * Stores authorities + token version inside the token.
     */
    public String generateToken(UserDetails userDetails) {

        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        int tokenVersion = 0;
        if (userDetails instanceof User u) {
            tokenVersion = u.getTokenVersion();
        }

        Instant now = Instant.now();

        log.debug("Generating JWT token for username={}", userDetails.getUsername());

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim(CLAIM_AUTHORITIES, authorities)
                .claim(CLAIM_TOKEN_VERSION, tokenVersion)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(signingKey)
                .compact();

        log.info("JWT token successfully generated for username={}", userDetails.getUsername());

        return token;
    }

    /* =========================
       Token validation
       ========================= */

    /**
     * Validates token signature and expiration.
     */
    public boolean isTokenValid(String token) {
        try {
            parseAndValidate(token);
            return true;
        } catch (Exception ex) {
            log.warn("JWT token validation failed");
            return false;
        }
    }

    /* =========================
       Claim extraction
       ========================= */

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);

        Object raw = claims.get(CLAIM_AUTHORITIES);
        if (raw instanceof List<?> list) {
            return list.stream()
                    .filter(v -> v != null)
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Extracts token version used for server-side revocation.
     */
    public int extractTokenVersion(String token) {
        Claims claims = extractAllClaims(token);

        Object raw = claims.get(CLAIM_TOKEN_VERSION);
        if (raw == null) {
            return 0;
        }
        try {
            return Integer.parseInt(raw.toString());
        } catch (Exception ex) {
            return 0;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {
        return resolver.apply(extractAllClaims(token));
    }

    /* =========================
       Internal helpers
       ========================= */

    private void parseAndValidate(String token) {
        Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
