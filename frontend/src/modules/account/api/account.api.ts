// src/modules/account/api/account.api.ts
//
// Account API client
//
// This module handles requests that require:
// - authenticated user (JWT)
// - cookies
// - CSRF protection
//
// IMPORTANT SECURITY MODEL:
// --------------------------------------------------
// - JWT is sent via Authorization header (Bearer token)
// - CSRF protection is REQUIRED for all account mutations
// - CSRF token MUST be fetched BEFORE the first protected request
// - CSRF token is stored in a cookie and reused automatically by Axios
//

import http from '@/app/api/http'

// --------------------------------------------------
// CSRF BOOTSTRAP STATE
// --------------------------------------------------
//
// This flag ensures that the CSRF token is fetched
// only once per browser session.
//
// Why this is needed:
// - CSRF token is stored in a cookie (XSRF-TOKEN)
// - Cookie persists between requests
// - No need to request a new token for every POST
//
// NOTE:
// - This is NOT a security mechanism
// - It is a performance and correctness optimization
//
let csrfReady = false

// --------------------------------------------------
// CSRF INITIALIZATION
// --------------------------------------------------
/**
 * Ensures that a CSRF token cookie exists in the browser.
 *
 * How it works:
 * --------------------------------------------------
 * 1. Sends GET /auth/csrf with cookies enabled
 * 2. Spring Security generates a CSRF token
 * 3. Token is sent back as a cookie: XSRF-TOKEN
 * 4. Browser stores the cookie
 * 5. Axios can now read the cookie and attach
 *    the X-XSRF-TOKEN header automatically
 *
 * Why this must be called BEFORE protected requests:
 * --------------------------------------------------
 * - CSRF header cannot be sent if the cookie does not exist yet
 * - The CSRF cookie is NOT created automatically by Spring
 *
 * Why it is safe to call multiple times:
 * --------------------------------------------------
 * - The function short-circuits using csrfReady
 * - Cookie already exists after the first call
 */
async function ensureCsrf(): Promise<void> {
  // If CSRF token is already initialized, do nothing
  if (csrfReady) return

  // Request CSRF token from backend
  //
  // withCredentials is REQUIRED so the browser
  // is allowed to store the Set-Cookie header
  await http.get('/auth/csrf', {
    withCredentials: true,
  })

  // Mark CSRF as initialized
  csrfReady = true
}

// --------------------------------------------------
// DTOs
// --------------------------------------------------

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
}

// --------------------------------------------------
// CHANGE PASSWORD
// --------------------------------------------------
/**
 * Change password of the currently authenticated user.
 *
 * Security flow:
 * --------------------------------------------------
 * 1. ensureCsrf() guarantees CSRF cookie exists
 * 2. Axios automatically attaches:
 *    - Authorization: Bearer <JWT>
 *    - X-XSRF-TOKEN header (read from cookie)
 * 3. Spring Security validates:
 *    - JWT (authentication)
 *    - CSRF token (request integrity)
 *
 * Expected backend responses:
 * --------------------------------------------------
 * - 204 No Content on success
 * - 401 if JWT is missing or invalid
 * - 403 if CSRF validation fails
 */
export async function changePassword(
  data: ChangePasswordRequest
): Promise<void> {
  // Make sure CSRF token is available BEFORE POST
  await ensureCsrf()

  // Send password change request
  await http.post('/account/change-password', data, {
    withCredentials: true, // REQUIRED for CSRF
  })
}
