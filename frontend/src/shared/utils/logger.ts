// src/shared/utils/logger.ts

import { enableLogs } from '@/app/config/env'

export const logger = {
  log: (...args: unknown[]) => {
    if (enableLogs) {
      console.log('[LOG]', ...args)
    }
  },

  warn: (...args: unknown[]) => {
    if (enableLogs) {
      console.warn('[WARN]', ...args)
    }
  },

  error: (...args: unknown[]) => {
    // Ошибки логируем ВСЕГДА (и в prod)
    console.error('[ERROR]', ...args)
  },
}
