// src/shared/utils/dateFormatter.ts

/**
 * Format a date string or Date object into a human-readable format.
 *
 * Examples:
 * - "2024-01-15T10:30:00" → "Jan 15, 2024"
 * - new Date() → "Just now" (if within last minute)
 */
export function formatDate(date: string | Date | null | undefined): string {
  if (!date) {
    return 'Unknown date'
  }

  const dateObj = typeof date === 'string' ? new Date(date) : date
  const now = new Date()
  const diffMs = now.getTime() - dateObj.getTime()
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  // Within last minute
  if (diffMins < 1) {
    return 'Just now'
  }

  // Within last hour
  if (diffHours < 1) {
    return `${diffMins} minute${diffMins > 1 ? 's' : ''} ago`
  }

  // Within last day
  if (diffDays < 1) {
    return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`
  }

  // Within last week
  if (diffDays < 7) {
    return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`
  }

  // Default: formatted date
  const options: Intl.DateTimeFormatOptions = {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  }

  return dateObj.toLocaleDateString('en-US', options)
}

/**
 * Format a date in long format.
 *
 * Examples:
 * - "2024-01-15T10:30:00" → "January 15, 2024 at 10:30 AM"
 */
export function formatDateLong(date: string | Date): string {
  const dateObj = typeof date === 'string' ? new Date(date) : date

  const options: Intl.DateTimeFormatOptions = {
    month: 'long',
    day: 'numeric',
    year: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
    hour12: true,
  }

  return dateObj.toLocaleDateString('en-US', options)
}
