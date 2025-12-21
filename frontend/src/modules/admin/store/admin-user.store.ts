import { ensureCsrf } from '@/app/security/csrf'
import type { AdminUser, UserRole } from '@/modules/admin/api/admin-user.api'
import * as adminApi from '@/modules/admin/api/admin-user.api'
import { defineStore } from 'pinia'

export const useAdminUserStore = defineStore('adminUser', {
  // --------------------------------------------------
  // State
  // --------------------------------------------------
  state: () => ({
    users: [] as AdminUser[],
    loading: false,
    error: null as string | null,
  }),

  // --------------------------------------------------
  // Actions
  // --------------------------------------------------
  actions: {
    /**
     * Initial / explicit reload (NON-optimistic).
     */
    async fetchUsers(): Promise<void> {
      this.loading = true
      this.error = null

      try {
        this.users = await adminApi.listUsers()
      } catch (e) {
        console.error('[ADMIN] fetch users failed', e)
        this.error = 'Failed to load users'
      } finally {
        this.loading = false
      }
    },


    /**
     * Change user role (OPTIMISTIC).
     */
    async changeRole(userId: string, role: UserRole): Promise<void> {
      this.error = null

      const previousUsers = [...this.users]

      this.users = this.users.map(u =>
        u.id === userId ? { ...u, role } : u
      )

      try {
        await adminApi.changeRole({ userId, role })
      } catch (e) {
        this.users = previousUsers
        this.error = 'Failed to change role'
      }
    },




    /**
     * Disable user (OPTIMISTIC).
     */
    async disableUser(userId: string): Promise<void> {
      this.error = null
      const previousUsers = [...this.users]

      this.users = this.users.map((u) =>
        u.id === userId ? { ...u, enabled: false } : u
      )

      try {
        await ensureCsrf()
        await adminApi.disableUser({ userId })

      } catch (e) {
        console.error('[ADMIN] disable user failed', e)
        this.users = previousUsers
        this.error = 'Failed to disable user'
      }
    },

    /**
     * Enable user (OPTIMISTIC).
     */
    async enableUser(userId: string): Promise<void> {
      this.error = null
      const previousUsers = [...this.users]

      this.users = this.users.map((u) =>
        u.id === userId ? { ...u, enabled: true } : u
      )

      try {
        await ensureCsrf()
        await adminApi.enableUser({ userId })
      } catch (e) {
        console.error('[ADMIN] enable user failed', e)
        this.users = previousUsers
        this.error = 'Failed to enable user'
      }
    },
  },
})
