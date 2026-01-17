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
// - Attach JWT if present (but NOT for public endpoints)
// - DO NOT modify cookie behavior
// --------------------------------------------------
http.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  const url = config.url || ''
  
  // Don't send JWT for public endpoints
  const isPublicEndpoint = url.includes('/api/public/')

  // Attach JWT token if available and not a public endpoint
  if (authStore.token && !isPublicEndpoint) {
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
    // Only force logout if the 401 is NOT from a public endpoint
    if (error.response?.status === 401) {
      const url = error.config?.url || ''
      const isPublicEndpoint = url.includes('/api/public/') || url.includes('/auth/')
      
      if (!isPublicEndpoint) {
        console.warn('[AUTH] 401 Unauthorized -> force logout')
        authStore.logout()
        router.push('/login')
      } else {
        console.log('[AUTH] 401 on public endpoint - ignoring')
      }
    }

    return Promise.reject(error)
  }
)

export default http
