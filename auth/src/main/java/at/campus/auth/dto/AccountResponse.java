package at.campus.auth.dto;

import at.campus.auth.model.UserRole;

import java.util.UUID;

public class AccountResponse {

    private UUID id;
    private String email;
    private String nickname;
    private UserRole role;

    public AccountResponse(
            UUID id,
            String email,
            String nickname,
            UserRole role
    ) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public UserRole getRole() {
        return role;
    }
}
