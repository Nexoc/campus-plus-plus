import * as authApi from '@/modules/auth/api/auth.api'
import { defineStore } from 'pinia'

interface AuthState {
  token: string | null
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('token'),
  }),

  getters: {
    /**
     * Returns true if user is authenticated.
     * Authentication is based on presence of JWT token.
     */
    isAuthenticated: (state): boolean => {
      return !!state.token
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
