// src/app/api/http.ts
//
// Axios HTTP client
//
// Design:
// - Single axios instance
// - JWT is ALWAYS sent via Authorization header
// - Cookies + CSRF are OPT-IN per request
// - /api/** works WITHOUT cookies -> NO CSRF
// - Auth / Account / Admin explicitly enable cookies when needed
//
// This file is intentionally simple:
// Axios interceptors already provide correct typings.
// Adding manual types here causes version conflicts.

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
  // If a request needs cookies + CSRF,
  // it MUST explicitly set withCredentials: true.
  withCredentials: false,

  // Native Axios CSRF support.
  // These are only used when withCredentials=true.
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
})

// --------------------------------------------------
// REQUEST INTERCEPTOR
// --------------------------------------------------
// Responsibilities:
// - Attach JWT if present
// - Log outgoing request data for debugging
// - Make cookie usage visible
// --------------------------------------------------
http.interceptors.request.use((config) => {
  const authStore = useAuthStore()

  // --------------------------------------------
  // AUTO-ENABLE COOKIES FOR STATE-CHANGING CALLS
  // --------------------------------------------
  if (
    config.method &&
    !['get', 'head', 'options'].includes(config.method)
  ) {
    config.withCredentials = true
  }

  // Attach JWT
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
// - Log responses
// - Central handling of 401 Unauthorized
// - Force logout on invalid / expired / revoked JWT
// --------------------------------------------------
http.interceptors.response.use(
  (response) => {
    console.group('[HTTP RESPONSE]')
    console.log('URL:', response.config.url)
    console.log('Status:', response.status)
    console.log('Response headers:', response.headers)
    console.log('Browser cookies after response:', document.cookie)
    console.groupEnd()

    return response
  },
  (error) => {
    console.group('[HTTP ERROR]')
    console.log('URL:', error.config?.url)
    console.log('Method:', error.config?.method?.toUpperCase())
    console.log('Status:', error.response?.status)
    console.log('Response headers:', error.response?.headers)
    console.log('Browser cookies at error:', document.cookie)
    console.groupEnd()

    const authStore = useAuthStore()

    // --------------------------------------------
    // EXPECTED CSRF BOOTSTRAP FAILURE (DO NOT FAIL)
    // --------------------------------------------
    if (
      error.config?.url === '/auth/csrf' &&
      error.response?.status === 403
    ) {
      // CSRF token has already been generated and saved
      return Promise.resolve(error.response)
    }

    // --------------------------------------------
    // REAL AUTH ERROR
    // --------------------------------------------
    if (error.response?.status === 401) {
      console.warn('[AUTH] 401 received -> logging out')
      authStore.logout()
      router.push('/login')
    }

    return Promise.reject(error)
  }
)






export default http
