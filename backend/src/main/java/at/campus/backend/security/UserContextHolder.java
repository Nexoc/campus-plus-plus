package at.campus.backend.security;

/**
 * ThreadLocal holder for UserContext.
 *
 * WHY ThreadLocal?
 * - Each HTTP request is processed in its own thread.
 * - ThreadLocal guarantees isolation between requests.
 * - No global/static user state is shared.
 *
 * VERY IMPORTANT:
 * - clear() MUST be called after every request
 *   to avoid memory leaks.
 *
 * This class intentionally has:
 * - no Spring annotations
 * - no dependency on HTTP or Security frameworks
 */
public final class UserContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
        // utility class
    }

    public static void set(UserContext context) {
        CONTEXT.set(context);
    }

    /**
     * Returns the UserContext for the current request,
     * or null if the request is unauthenticated
     * (e.g. internal or public endpoints).
     */
    public static UserContext get() {
        return CONTEXT.get();
    }

    /**
     * MUST be called in a finally block.
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
