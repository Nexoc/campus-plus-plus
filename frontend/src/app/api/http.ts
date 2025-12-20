// src/app/api/http.ts
//
// Central HTTP client for the application.
// Responsibilities:
// - configure axios
// - attach JWT token to requests (if present)
// - attach CSRF token to requests (if present)
// - handle global auth errors (401)
// - redirect user to login when session expires
//
// Infrastructure only.

import router from '@/app/router'
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { logger } from '@/shared/utils/logger'
import axios from 'axios'

// --------------------------------------------------
// Helper: read cookie value by name
// --------------------------------------------------
function getCookie(name: string): string | null {
  const match = document.cookie.match(
    new RegExp('(^| )' + name + '=([^;]+)')
  )
  return match ? decodeURIComponent(match[2]) : null
}

// --------------------------------------------------
// Axios instance
// --------------------------------------------------
const http = axios.create({
  baseURL: '/',
  withCredentials: true,
})

// --------------------------------------------------
// REQUEST INTERCEPTOR
// --------------------------------------------------
http.interceptors.request.use((config) => {
  const authStore = useAuthStore()

  // JWT
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
    logger.log('[http] JWT attached')
  }

  // CSRF
  const csrfToken = getCookie('XSRF-TOKEN')
  if (csrfToken) {
    config.headers['X-XSRF-TOKEN'] = csrfToken
    logger.log('[http] CSRF attached')
  }

  return config
})

// --------------------------------------------------
// RESPONSE INTERCEPTOR
// --------------------------------------------------
http.interceptors.response.use(
  (response) => response,
  (error) => {
    const authStore = useAuthStore()

    if (error.response?.status === 401) {
      logger.warn('[http] 401 → logout')

      authStore.logout()

      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
    }

    return Promise.reject(error)
  }
)

export default http
