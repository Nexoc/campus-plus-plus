// src/modules/account/store/account.store.ts
//
// Account Store (Pinia)
//
// Responsibilities:
// --------------------------------------------------
// - Orchestrate account-related user actions
// - Delegate all HTTP and security logic to API layer
//
// Design notes:
// --------------------------------------------------
// - Store does NOT handle CSRF directly
// - Store does NOT know about cookies or headers
// - All security concerns are encapsulated in account.api.ts
//

import * as accountApi from '@/modules/account/api/account.api'
import { logger } from '@/shared/utils/logger'
import { defineStore } from 'pinia'

export const useAccountStore = defineStore('account', {
  actions: {
    /**
     * Change password of the currently authenticated user.
     *
     * Flow:
     * 1. Delegate request to API layer
     * 2. API layer ensures CSRF + JWT
     * 3. Store handles only control flow and logging
     *
     * Errors:
     * - Any error is logged
     * - Error is re-thrown for UI handling
     */
    async changePassword(
      currentPassword: string,
      newPassword: string
    ): Promise<void> {
      logger.log('ACCOUNT changePassword started')

      try {
        await accountApi.changePassword({
          currentPassword,
          newPassword,
        })

        logger.log('ACCOUNT changePassword successful')
      } catch (error) {
        logger.error('ACCOUNT changePassword failed', error)
        throw error
      }
    },
  },
})
