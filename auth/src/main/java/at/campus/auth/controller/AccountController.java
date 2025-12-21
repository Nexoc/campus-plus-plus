package at.campus.auth.controller;

import at.campus.auth.dto.AccountResponse;
import at.campus.auth.dto.ChangePasswordRequest;
import at.campus.auth.model.User;
import at.campus.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


/**
 * AccountController
 * Endpoints for authenticated users.
 */
@RestController
@RequestMapping("/account")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AuthService authService;

    public AccountController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(
                request.getCurrentPassword(),
                request.getNewPassword()
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AccountResponse> me() {

        User user = authService.getCurrentUser();

        AccountResponse response = new AccountResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }
}

