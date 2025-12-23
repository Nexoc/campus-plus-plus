package at.campus.backend.common.api;

import at.campus.backend.security.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Debug-only controller.
 *
 * PURPOSE:
 * - Verify that identity headers injected by Nginx
 *   are correctly propagated into backend.
 * - MUST NOT contain business logic.
 *
 * This controller is typically:
 * - disabled or removed in production
 * - protected at the gateway level
 */
@RestController
public class DebugController {

    private final UserContext userContext;

    public DebugController(UserContext userContext) {
        this.userContext = userContext;
    }

    @GetMapping("/api/debug/me")
    public Map<String, Object> me() {

        if (userContext.getUserId() == null) {
            return Map.of(
                    "authenticated", false,
                    "message", "NO USER CONTEXT"
            );
        }

        return Map.of(
                "authenticated", true,
                "userId", userContext.getUserId(),
                "roles", userContext.getRoles()
        );
    }
}


