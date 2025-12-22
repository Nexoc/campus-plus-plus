package at.campus.backend.common.api;

import at.campus.backend.security.UserContext;
import at.campus.backend.security.UserContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/api/debug/me")
    public Object me() {
        UserContext context = UserContextHolder.get();

        if (context == null) {
            return "NO USER CONTEXT (request is unauthenticated or internal)";
        }

        return context;
    }
}
