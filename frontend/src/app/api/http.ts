// src/app/api/http.ts
//
// Axios HTTP client
//
// Design principles:
// --------------------------------------------------
// - Single axios instance for the entire application
// - JWT is ALWAYS sent via Authorization header
// - Cookies + CSRF are EXPLICIT and OPT-IN per request
// - No implicit security decisions in interceptors
// - NGINX is the security gateway, frontend stays dumb
//
// This file is intentionally simple and predictable.

import router from '@/app/router'
import { useAuthStore } from '@/modules/auth/store/auth.store'
import axios from 'axios'

// --------------------------------------------------
// AXIOS INSTANCE
// --------------------------------------------------
const http = axios.create({
  baseURL: '/',

  // IMPORTANT:
  // Cookies are DISABLED globally.
  // Any request that needs cookies or CSRF
  // MUST explicitly set withCredentials: true.
  withCredentials: false,

  // Axios built-in CSRF support.
  // These values are only used when withCredentials=true.
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
})

// --------------------------------------------------
// REQUEST INTERCEPTOR
// --------------------------------------------------
// Responsibilities:
// - Attach JWT if present
// - DO NOT modify cookie behavior
// --------------------------------------------------
http.interceptors.request.use((config) => {
  const authStore = useAuthStore()

  // Attach JWT token if available
  if (authStore.token) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${authStore.token}`
  }

  return config
})

// --------------------------------------------------
// RESPONSE INTERCEPTOR
// --------------------------------------------------
// Responsibilities:
// - Handle authentication failures globally
// - Allow CSRF bootstrap endpoint to fail gracefully
// --------------------------------------------------
http.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    const authStore = useAuthStore()

    // --------------------------------------------
    // EXPECTED CSRF BOOTSTRAP FAILURE
    // --------------------------------------------
    // POST /auth/csrf intentionally returns 403.
    // Spring Security still creates the XSRF-TOKEN
    // cookie before rejecting the request.
    if (
      error.config?.url === '/auth/csrf' &&
      error.response?.status === 403
    ) {
      return Promise.resolve(error.response)
    }

    // --------------------------------------------
    // REAL AUTHENTICATION FAILURE
    // --------------------------------------------
    if (error.response?.status === 401) {
      console.warn('[AUTH] 401 Unauthorized -> force logout')

      authStore.logout()
      router.push('/login')
    }

    return Promise.reject(error)
  }
)

export default http
