// src/modules/account/store/account.store.ts
//
// Account Store
//
// Responsibilities:
// - Orchestrate account-related user actions
// - Delegate all HTTP and security logic to API layer
//
// IMPORTANT:
// - CSRF logic is handled INSIDE account.api.ts
// - Store must NOT handle CSRF state or bootstrap

import * as accountApi from '@/modules/account/api/account.api'
import { defineStore } from 'pinia'

export const useAccountStore = defineStore('account', {
  actions: {
    /**
     * Change password of the currently authenticated user.
     *
     * Flow:
     * - Delegate CSRF bootstrap + request to API layer
     * - Handle only logging and control flow
     */
    async changePassword(
      currentPassword: string,
      newPassword: string
    ): Promise<void> {
      console.log('[ACCOUNT STORE] change password start')

      await accountApi.changePassword({
        currentPassword,
        newPassword,
      })

      console.log('[ACCOUNT STORE] change password success')
    },
  },
})
