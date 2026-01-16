<template>
  <Teleport to="body">
    <div class="toast-container">
      <TransitionGroup name="toast">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          :class="['toast', `toast--${toast.type}`]"
        >
          <div class="toast__content">
            <span class="toast__icon">{{ getIcon(toast.type) }}</span>
            <span class="toast__message">{{ toast.message }}</span>
          </div>
          <button class="toast__close" @click="removeToast(toast.id)">
            ×
          </button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref } from 'vue'

export interface Toast {
  id: number
  message: string
  type: 'success' | 'error' | 'info' | 'warning'
  duration?: number
}

const toasts = ref<Toast[]>([])
let idCounter = 0

function getIcon(type: Toast['type']): string {
  const icons = {
    success: '✓',
    error: '✕',
    info: 'ⓘ',
    warning: '⚠',
  }
  return icons[type]
}

function addToast(message: string, type: Toast['type'] = 'info', duration = 3000) {
  const id = ++idCounter
  const toast: Toast = { id, message, type, duration }
  
  toasts.value.push(toast)

  if (duration > 0) {
    setTimeout(() => {
      removeToast(id)
    }, duration)
  }
}

function removeToast(id: number) {
  const index = toasts.value.findIndex(t => t.id === id)
  if (index > -1) {
    toasts.value.splice(index, 1)
  }
}

defineExpose({ addToast })
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 10000;
  display: flex;
  flex-direction: column;
  gap: 10px;
  pointer-events: none;
  max-width: 400px;
}

.toast {
  pointer-events: auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-width: 300px;
  max-width: 500px;
  padding: 12px 16px;
  border-radius: 8px;
  background: var(--card-bg, #fff);
  border: 1px solid var(--border-color, #e0e0e0);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  font-size: 14px;
}

.toast__content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toast__icon {
  font-size: 18px;
  font-weight: bold;
}

.toast__message {
  flex: 1;
  color: var(--text-color, #333);
}

.toast__close {
  background: none;
  border: none;
  font-size: 24px;
  line-height: 1;
  cursor: pointer;
  padding: 0;
  margin-left: 12px;
  color: var(--text-secondary, #666);
  opacity: 0.7;
  transition: opacity 0.2s;
}

.toast__close:hover {
  opacity: 1;
}

/* Toast type styles */
.toast--success {
  border-left: 4px solid #10b981;
}

.toast--success .toast__icon {
  color: #10b981;
}

.toast--error {
  border-left: 4px solid #ef4444;
}

.toast--error .toast__icon {
  color: #ef4444;
}

.toast--info {
  border-left: 4px solid #3b82f6;
}

.toast--info .toast__icon {
  color: #3b82f6;
}

.toast--warning {
  border-left: 4px solid #f59e0b;
}

.toast--warning .toast__icon {
  color: #f59e0b;
}

/* Transitions */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100%) scale(0.9);
}
</style>
