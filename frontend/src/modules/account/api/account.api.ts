// src/modules/account/api/account.api.ts
//
// Account API client
//
// Responsibilities:
// --------------------------------------------------
// - Perform authenticated account-related requests
// - Ensure CSRF protection for all state-changing operations
//
// Security model:
// --------------------------------------------------
// - JWT is sent via Authorization header (handled by Axios interceptor)
// - CSRF protection is REQUIRED for all account mutations
// - CSRF token is bootstrapped via POST /auth/csrf
// - 403 from /auth/csrf is EXPECTED and IGNORED
//

import http from '@/app/api/http'
import { ensureCsrf } from '@/app/security/csrf'

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
 * Flow:
 * --------------------------------------------------
 * 1. POST /auth/csrf
 *    - Forces Spring Security to generate CSRF token
 *    - 403 response is expected and ignored
 * 2. POST /account/change-password
 *    - Axios automatically attaches:
 *        - Authorization: Bearer <JWT>
 *        - X-XSRF-TOKEN header (from cookie)
 *
 * Expected responses:
 * --------------------------------------------------
 * - 204 No Content on success
 * - 401 if JWT is invalid or expired
 * - 403 if CSRF validation fails
 */
export async function changePassword(
  data: ChangePasswordRequest
): Promise<void> {
  // ALWAYS bootstrap CSRF before password change
  await ensureCsrf()

  // Perform password change
  await http.post('/account/change-password', data, {
    withCredentials: true,
  })
}
