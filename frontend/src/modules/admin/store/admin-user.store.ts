import * as adminApi from '@/modules/admin/api/admin-user.api'
import { defineStore } from 'pinia'

export const useAdminUserStore = defineStore('adminUser', {
  actions: {
    async changeRole(userId: number, role: string): Promise<void> {
      console.log('[ADMIN] change role', userId, role)
      await adminApi.changeRole({ userId, role })
    },

    async disableUser(userId: number): Promise<void> {
      console.log('[ADMIN] disable user', userId)
      await adminApi.disableUser({ userId })
    },

    async enableUser(userId: number): Promise<void> {
      console.log('[ADMIN] enable user', userId)
      await adminApi.enableUser({ userId })
    },
  },
})
