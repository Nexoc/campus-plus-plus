// src/shared/composables/useToast.ts

// Global toast instance
let toastInstance: any = null

export function setToastInstance(instance: any) {
  toastInstance = instance
}

export function useToast() {
  function success(message: string, duration = 3000) {
    toastInstance?.addToast(message, 'success', duration)
  }

  function error(message: string, duration = 4000) {
    toastInstance?.addToast(message, 'error', duration)
  }

  function info(message: string, duration = 3000) {
    toastInstance?.addToast(message, 'info', duration)
  }

  function warning(message: string, duration = 3000) {
    toastInstance?.addToast(message, 'warning', duration)
  }

  return {
    success,
    error,
    info,
    warning,
  }
}
