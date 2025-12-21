import http from '@/app/api/http'

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
  nickname?: string
}

export interface AuthResponse {
  token: string
}

/**
 * User registration.
 * NO cookies.
 * NO CSRF.
 */
export async function register(
  data: RegisterRequest
): Promise<void> {
  await http.post('/auth/register', data)
}

/**
 * User login.
 * NO cookies.
 * NO CSRF.
 * JWT is returned in response body.
 */
export async function login(
  data: LoginRequest
): Promise<AuthResponse> {
  const response = await http.post<AuthResponse>(
    '/auth/login',
    data
  )
  return response.data
}
