import http from '@/app/api/http'

// --------------------------------------------------
// DTOs
// --------------------------------------------------

export type UserRole = 'VISITOR' | 'STUDENT' | 'Moderator'

export interface AdminUser {
  id: string
  email: string
  nickname: string | null
  role: UserRole
  enabled: boolean
  accountNonLocked: boolean
  createdAt: string
}

export interface AdminChangeRoleRequest {
  userId: string
  role: UserRole
}

export interface AdminUserStatusRequest {
  userId: string
}

// --------------------------------------------------
// API calls
// --------------------------------------------------

export async function listUsers(): Promise<AdminUser[]> {
  const response = await http.get<AdminUser[]>('/admin/users')
  return response.data
}

export async function changeRole(
  data: AdminChangeRoleRequest
): Promise<void> {
  await http.post('/admin/users/change-role', data, {
    withCredentials: true,
  })
}

export async function disableUser(
  data: AdminUserStatusRequest
): Promise<void> {
  await http.post('/admin/users/disable', data, {
    withCredentials: true,
  })
}

export async function enableUser(
  data: AdminUserStatusRequest
): Promise<void> {
  await http.post('/admin/users/enable', data, {
    withCredentials: true,
  })
}
