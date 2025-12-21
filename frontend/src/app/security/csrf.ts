import http from '@/app/api/http'

export async function ensureCsrf(): Promise<void> {
  try {
    await http.post('/auth/csrf', {}, { withCredentials: true })
  } catch (e) {
    // IMPORTANT:
    // 403 is EXPECTED here.
    // Spring Security creates the CSRF token
    // and writes it to the XSRF-TOKEN cookie
    // BEFORE rejecting the request.
  }
}
