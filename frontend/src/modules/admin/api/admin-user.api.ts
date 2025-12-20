import http from '@/app/api/http'

// --------------------------------------------------
// CSRF bootstrap (shared with account)
// --------------------------------------------------
let csrfReady = false

async function ensureCsrf(): Promise<void> {
  if (csrfReady) return

  await http.get('/auth/csrf', {
    withCredentials: true,
  })

  csrfReady = true
}

// --------------------------------------------------
// DTOs
// --------------------------------------------------
export interface AdminChangeRoleRequest {
  userId: number
  role: string
}

export interface AdminUserStatusRequest {
  userId: number
}

// --------------------------------------------------
// API calls
// --------------------------------------------------

/**
 * Change role of a user.
 * Requires ROLE_ADMIN.
 */
export async function changeRole(
  data: AdminChangeRoleRequest
): Promise<void> {
  await ensureCsrf()

  await http.post('/admin/users/change-role', data, {
    withCredentials: true,
  })
}

/**
 * Disable (ban) a user.
 */
export async function disableUser(
  data: AdminUserStatusRequest
): Promise<void> {
  await ensureCsrf()

  await http.post('/admin/users/disable', data, {
    withCredentials: true,
  })
}

/**
 * Enable (unban) a user.
 */
export async function enableUser(
  data: AdminUserStatusRequest
): Promise<void> {
  await ensureCsrf()

  await http.post('/admin/users/enable', data, {
    withCredentials: true,
  })
}
