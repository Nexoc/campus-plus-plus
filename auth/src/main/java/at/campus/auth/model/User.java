package at.campus.auth.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    // --------------------------------------------------
    // Identity
    // --------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    // --------------------------------------------------
    // Profile (NOT used for auth)
    // --------------------------------------------------
    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    // --------------------------------------------------
    // Security
    // --------------------------------------------------

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private int tokenVersion = 0;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean accountNonLocked = true;

    protected User() {
        // JPA only
    }

    public User(String email, String nickname, String passwordHash, UserRole role) {
        this.email = email;
        this.nickname = nickname;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = Instant.now();
    }

    // --------------------------------------------------
    // Lifecycle
    // --------------------------------------------------

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    // --------------------------------------------------
    // Spring Security
    // --------------------------------------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return accountNonLocked; }
    @Override public boolean isEnabled() { return enabled; }

    // --------------------------------------------------
    // Getters
    // --------------------------------------------------

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public UserRole getRole() { return role; }
    public Instant getCreatedAt() { return createdAt; }
    public int getTokenVersion() { return tokenVersion; }

    // --------------------------------------------------
    // Mutators (domain-safe)
    // --------------------------------------------------

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePasswordHash(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        bumpTokenVersion();
    }

    public void changeRole(UserRole newRole) {
        this.role = newRole;
        bumpTokenVersion();
    }

    public void disable() {
        this.enabled = false;
        bumpTokenVersion();
    }

    public void enable() {
        this.enabled = true;
        bumpTokenVersion();
    }

    public void lock() {
        this.accountNonLocked = false;
        bumpTokenVersion();
    }

    public void unlock() {
        this.accountNonLocked = true;
        bumpTokenVersion();
    }

    private void bumpTokenVersion() {
        this.tokenVersion++;
    }
}
