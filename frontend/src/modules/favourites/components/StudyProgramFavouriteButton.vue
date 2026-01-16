<template>
  <button
    class="favourite-button"
    :class="{ 
      'favourite-button--active': isFavourited, 
      'favourite-button--loading': isLoading,
      'favourite-button--compact': !showLabel
    }"
    :disabled="isLoading || !isAuthenticated"
    @click="toggleFavourite"
    :title="tooltipText"
  >
    <StarIcon :size="size" :filled="isFavourited" />
    <span v-if="showLabel" class="favourite-button__label">
      {{ isFavourited ? 'Remove from favourites' : 'Add to favourites' }}
    </span>
  </button>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import StarIcon from '@/shared/components/icons/StarIcon.vue'
import { useFavouritesStore } from '../store/favourites.store'
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { useToast } from '@/shared/composables/useToast'

const props = withDefaults(
  defineProps<{
    studyProgramId: string
    size?: number
    showLabel?: boolean
  }>(),
  {
    size: 20,
    showLabel: true
  }
)

const favouritesStore = useFavouritesStore()
const authStore = useAuthStore()
const toast = useToast()

const isAuthenticated = computed(() => authStore.isAuthenticated)
const isLoading = ref(false)

const isFavourited = computed(() => favouritesStore.isStudyProgramFavourite(props.studyProgramId))

const tooltipText = computed(() => {
  if (!isAuthenticated.value) return 'Login to save favourites'
  return isFavourited.value ? 'Remove from favourites' : 'Add to favourites'
})

async function toggleFavourite() {
  if (!isAuthenticated.value) return

  isLoading.value = true
  try {
    if (isFavourited.value) {
      await favouritesStore.removeStudyProgramFavourite(props.studyProgramId)
      toast.success('Removed from favourites')
    } else {
      await favouritesStore.addStudyProgramFavourite(props.studyProgramId)
      toast.success('Added to favourites')
    }
  } catch (error: any) {
    const message = error?.response?.data?.message || 'Failed to update favourites'
    toast.error(message)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.favourite-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 20px;
  background: white;
  border: 2px solid var(--star-color, #fbbf24);
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--star-color, #fbbf24);
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.favourite-button:hover:not(:disabled) {
  background: var(--star-color, #fbbf24);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(251, 191, 36, 0.3);
}

.favourite-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.favourite-button--active {
  background: var(--star-color, #fbbf24);
  color: white;
  border-color: var(--star-color, #fbbf24);
}

.favourite-button--active:hover:not(:disabled) {
  background: #f59e0b;
  border-color: #f59e0b;
}

.favourite-button--loading {
  opacity: 0.7;
}

.favourite-button__label {
  white-space: nowrap;
}

/* Icon animation on click */
.favourite-button:active:not(:disabled) :deep(.star-icon) {
  transform: scale(1.2);
}
</style>
