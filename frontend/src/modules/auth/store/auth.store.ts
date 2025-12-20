// src/modules/auth/store/auth.store.ts
//
// Authentication state store (Pinia).
// Responsible for:
// - storing JWT token
// - exposing authentication state
// - performing login / registration actions
// - handling logout
//
// IMPORTANT:
// - This store contains BUSINESS logic
// - It does NOT perform HTTP calls directly (delegates to API layer)
// - It does NOT handle routing or navigation
// - It does NOT handle UI concerns
//
// This store represents the single source of truth
// for authentication state on the frontend.

import { login as loginApi, register as registerApi } from '@/modules/auth/api/auth.api'
import { logger } from '@/shared/utils/logger'
import { defineStore } from 'pinia'

// --------------------------------------------------
// State definition
// --------------------------------------------------
//
// JWT token is stored both:
// - in Pinia state (reactive)
// - in localStorage (to survive page reloads)
//
interface AuthState {
  token: string | null
}

// --------------------------------------------------
// Store definition
// --------------------------------------------------
export const useAuthStore = defineStore('auth', {
  // --------------------------------------------------
  // Initial state
  // --------------------------------------------------
  //
  // On application startup, token is restored
  // from localStorage if present.
  //
  state: (): AuthState => ({
    token: localStorage.getItem('token'),
  }),

  // --------------------------------------------------
  // Getters
  // --------------------------------------------------
  //
  // Derived state that indicates whether the user
  // is currently authenticated.
  //
  getters: {
    isAuthenticated: (state): boolean => !!state.token,
  },

  // --------------------------------------------------
  // Actions
  // --------------------------------------------------
  //
  // Actions encapsulate business logic and side effects.
  //
  actions: {
    /**
     * Authenticate user using email and password.
     * On success:
     * - JWT token is stored in state
     * - JWT token is persisted in localStorage
     *
     * Errors are NOT caught here:
     * - Validation errors are handled by UI
     * - 401 errors are handled globally in http.ts
     */
    async login(email: string, password: string): Promise<void> {
      logger.log('[auth] login started')

      const response = await loginApi({ email, password })

      this.token = response.token
      localStorage.setItem('token', response.token)

      logger.log('[auth] login successful')
    },

    /**
     * Register a new user.
     * This action only performs registration.
     * It does NOT automatically log the user in.
     *
     * Decision to log in after registration
     * should be handled explicitly in UI layer.
     */
    async register(email: string, password: string): Promise<void> {
      logger.log('[auth] registration started')

      await registerApi({ email, password })

      logger.log('[auth] registration successful')
    },

    /**
     * Clear authentication state.
     * This is used when:
     * - user explicitly logs out
     * - backend invalidates the token (401)
     */
    logout(): void {
      logger.log('[auth] logout')

      this.token = null
      localStorage.removeItem('token')
    },
  },
})
