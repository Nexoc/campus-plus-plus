<template>
  <div class="watch-button">
    <button
      :class="['watch-btn', { watching: watchData?.isWatching, disabled: !canWatch }]"
      @click="toggleWatch"
      :disabled="!canWatch || loading"
      :title="getButtonTitle()"
    >
      <span class="icon">{{ watchData?.isWatching ? '👁️' : '👁️‍🗨️' }}</span>
      <span class="text">{{ watchData?.isWatching ? 'Watching' : 'Watch' }}</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { watchApi } from '@/shared/api/watch.api'
import type { WatchDto } from '@/shared/model/Watch'
import { useToast } from '@/shared/composables/useToast'

interface Props {
  targetType: 'COURSE' | 'THREAD'
  targetId: string
  notificationsEnabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  notificationsEnabled: true
})

const authStore = useAuthStore()
const toast = useToast()

const watchData = ref<WatchDto | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

const canWatch = computed(() => authStore.isAuthenticated)

function getButtonTitle(): string {
  if (!canWatch.value) {
    return 'Login to watch'
  }
  return watchData.value?.isWatching 
    ? 'Click to unwatch (stop notifications)' 
    : 'Click to watch (get notifications)'
}

async function loadWatchStatus() {
  try {
    watchData.value = await watchApi.getWatchStatus(props.targetType, props.targetId)
  } catch (err) {
    console.error('Failed to load watch status:', err)
    watchData.value = {
      id: null,
      targetType: props.targetType,
      targetId: props.targetId,
      notificationsEnabled: false,
      isWatching: false
    }
  }
}

async function toggleWatch() {
  if (!canWatch.value || loading.value) return

  loading.value = true
  error.value = null

  try {
    const isWatching = watchData.value?.isWatching || false
    const targetName = props.targetType === 'COURSE' ? 'course' : 'thread'
    const userEmail = authStore.user?.email

    if (isWatching) {
      // Unwatch
      await watchApi.unwatch({
        targetType: props.targetType,
        targetId: props.targetId
      })
      
      // Update local state
      if (watchData.value) {
        watchData.value.isWatching = false
      }
      
      toast.info(`You've unwatched this ${targetName}. You will no longer receive notifications.`)
    } else {
      // Watch
      const result = await watchApi.watch({
        targetType: props.targetType,
        targetId: props.targetId,
        notificationsEnabled: props.notificationsEnabled
      })
      
      watchData.value = result
      
      if (userEmail) {
        toast.success(`Successfully subscribed! You'll receive notifications at ${userEmail} for updates on this ${targetName}.`)
      } else {
        toast.success(`Successfully subscribed! You'll receive notifications for updates on this ${targetName}.`)
      }
    }
  } catch (err: any) {
    console.error('Failed to toggle watch:', err)
    error.value = err.response?.data?.message || 'Failed to update watch status'
    toast.error(error.value)
    // Reload to get correct state
    await loadWatchStatus()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadWatchStatus()
})
</script>

<style scoped>
.watch-button {
  display: inline-block;
}

.watch-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: var(--color-background);
  color: var(--color-text-primary);
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.95rem;
}

.watch-btn:hover:not(.disabled):not(:disabled) {
  background: var(--color-background-secondary);
  border-color: var(--color-border-hover);
}

.watch-btn.watching {
  background: #28a745;
  border-color: #28a745;
  color: white;
}

.watch-btn.watching:hover:not(:disabled) {
  background: #218838;
  border-color: #218838;
}

.watch-btn.disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.watch-btn:disabled {
  cursor: wait;
  opacity: 0.7;
}

.icon {
  font-size: 1.1rem;
  line-height: 1;
}

.text {
  font-weight: 500;
}
</style>
