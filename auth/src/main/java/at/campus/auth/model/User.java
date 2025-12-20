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

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
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

    public User(String email, String passwordHash, UserRole role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = Instant.now();
    }

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

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public UUID getId() {
        return id;
    }

    public UserRole getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public int getTokenVersion() {
        return tokenVersion;
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

    public void bumpTokenVersion() {
        this.tokenVersion++;
    }
}
