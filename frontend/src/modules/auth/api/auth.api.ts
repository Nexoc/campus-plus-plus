// src/modules/auth/api/auth.api.ts
//
// Auth API layer.
// HTTP only. No state, no UI, no routing.

import http from '@/app/api/http'

// --------------------------------------------------
// DTOs
// --------------------------------------------------
export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
}

export interface AuthResponse {
  token: string
}

// --------------------------------------------------
// CSRF bootstrap
// --------------------------------------------------
export async function initCsrf(): Promise<void> {
  await http.get('/auth/csrf')
}

// --------------------------------------------------
// API calls
// --------------------------------------------------
export async function login(
  data: LoginRequest
): Promise<AuthResponse> {
  const response = await http.post<AuthResponse>(
    '/auth/login',
    data
  )
  return response.data
}

export async function register(
  data: RegisterRequest
): Promise<void> {
  await http.post('/auth/register', data)
}
