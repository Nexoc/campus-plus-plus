<template>
  <div class="reaction-button">
    <button
      :class="['like-btn', { liked: reactionData?.currentUserLiked, disabled: !canReact }]"
      @click="toggleReaction"
      :disabled="!canReact || loading"
      :title="canReact ? (reactionData?.currentUserLiked ? 'Remove like' : 'Like') : 'Login to react'"
    >
      <span class="icon">👍</span>
      <span class="count">{{ reactionData?.likeCount || 0 }}</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { reactionApi } from '@/shared/api/reaction.api'
import type { ReactionCount } from '@/shared/model/Reaction'
import { useToast } from '@/shared/composables/useToast'

interface Props {
  targetType: 'post' | 'review'
  targetId: string
}

const props = defineProps<Props>()
const authStore = useAuthStore()
const toast = useToast()

const reactionData = ref<ReactionCount | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

const canReact = computed(() => authStore.isAuthenticated)

async function loadReactions() {
  try {
    if (props.targetType === 'post') {
      reactionData.value = await reactionApi.getPostReactions(props.targetId)
    } else {
      reactionData.value = await reactionApi.getReviewReactions(props.targetId)
    }
  } catch (err) {
    console.error('Failed to load reactions:', err)
    // Set default values on error
    reactionData.value = { likeCount: 0, currentUserLiked: false }
  }
}

async function toggleReaction() {
  if (!canReact.value || loading.value) return

  loading.value = true
  error.value = null

  try {
    const wasLiked = reactionData.value?.currentUserLiked || false

    if (wasLiked) {
      // Remove reaction
      if (props.targetType === 'post') {
        await reactionApi.removePostReaction(props.targetId)
      } else {
        await reactionApi.removeReviewReaction(props.targetId)
      }
    } else {
      // Add reaction
      if (props.targetType === 'post') {
        await reactionApi.addPostReaction(props.targetId)
      } else {
        await reactionApi.addReviewReaction(props.targetId)
      }
    }

    // Optimistic update
    if (reactionData.value) {
      reactionData.value.currentUserLiked = !wasLiked
      reactionData.value.likeCount += wasLiked ? -1 : 1
    }
  } catch (err: any) {
    console.error('Failed to toggle reaction:', err)
    error.value = err.response?.data?.message || 'Failed to update reaction'
    toast.error(error.value)
    // Reload to get correct state
    await loadReactions()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadReactions()
})
</script>

<style scoped>
.reaction-button {
  display: inline-block;
}

.like-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.4rem 0.8rem;
  border: 1px solid var(--color-border);
  border-radius: 20px;
  background: var(--color-background);
  color: var(--color-text-primary);
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.9rem;
}

.like-btn:hover:not(.disabled):not(:disabled) {
  background: var(--color-background-secondary);
  border-color: var(--color-border-hover);
}

.like-btn.liked {
  background: #4a7fb8;
  border-color: #4a7fb8;
  color: white;
}

.like-btn.liked:hover:not(:disabled) {
  background: #3a6fa8;
  border-color: #3a6fa8;
}

.like-btn.disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.like-btn:disabled {
  cursor: wait;
  opacity: 0.7;
}

.icon {
  font-size: 1rem;
  line-height: 1;
}

.count {
  font-weight: 500;
  min-width: 1ch;
}
</style>
