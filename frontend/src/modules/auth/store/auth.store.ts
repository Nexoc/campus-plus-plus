// src/modules/auth/store/auth.store.ts
//
// Auth Store
//
// Responsibilities:
// - store JWT
// - expose authentication state
// - expose user roles (parsed from JWT)

import * as authApi from '@/modules/auth/api/auth.api'
import { defineStore } from 'pinia'

interface AuthState {
  token: string | null
}

function parseJwt(token: string): any {
  try {
    const payload = token.split('.')[1]
    return JSON.parse(atob(payload))
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('token'),
  }),

  getters: {
    /**
     * User is authenticated if JWT exists.
     */
    isAuthenticated: (state): boolean => {
      return !!state.token
    },

    /**
     * Extract roles from JWT (authorities claim).
     */
    roles: (state): string[] => {
      if (!state.token) return []

      const payload = parseJwt(state.token)
      return payload?.authorities ?? []
    },

    /**
     * Admin check.
     */
    isAdmin(): boolean {
      return this.roles.includes('ROLE_ADMIN')
    },
  },

  actions: {
    async register(email: string, password: string): Promise<void> {
      await authApi.register({ email, password })
    },

    async login(email: string, password: string): Promise<void> {
      const response = await authApi.login({ email, password })
      this.token = response.token
      localStorage.setItem('token', response.token)
    },

    logout(): void {
      this.token = null
      localStorage.removeItem('token')
    },
  },
})
