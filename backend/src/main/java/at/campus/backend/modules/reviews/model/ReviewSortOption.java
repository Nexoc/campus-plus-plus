package at.campus.backend.modules.reviews.model;

/**
 * Enum for review sorting options.
 */
public enum ReviewSortOption {
    NEWEST("created_at DESC"),
    OLDEST("created_at ASC"),
    HIGHEST_RATING("rating DESC, created_at DESC"),
    LOWEST_RATING("rating ASC, created_at DESC");

    private final String sqlOrderBy;

    ReviewSortOption(String sqlOrderBy) {
        this.sqlOrderBy = sqlOrderBy;
    }

    public String getSqlOrderBy() {
        return sqlOrderBy;
    }

    /**
     * Parse a sort string to ReviewSortOption.
     * Returns NEWEST as default if the string is invalid or null.
     */
    public static ReviewSortOption fromString(String sort) {
        if (sort == null || sort.isEmpty()) {
            return NEWEST;
        }
        
        try {
            return valueOf(sort.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NEWEST;
        }
    }
}
