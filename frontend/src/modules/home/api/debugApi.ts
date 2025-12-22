import http from '@/app/api/http'

/**
 * Debug API
 *
 * Calls backend endpoint:
 *   GET /api/debug/me
 *
 * Security model:
 * - JWT automatically attached by request interceptor
 * - No cookies
 * - No CSRF
 * - Nginx performs auth_request
 */
export async function getDebugMe() {
  const response = await http.get('/api/debug/me')
  return response.data
}
