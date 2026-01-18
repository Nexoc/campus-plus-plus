<template>
  <div class="list-page">
    <div class="page-card">
      <h1>My Favourites</h1>
      <p v-if="!loading" class="results-info">
        {{ totalCount }} {{ totalCount === 1 ? 'item' : 'items' }}
      </p>

      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <p>Loading your favourites...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="favourites.length === 0 && studyProgramFavourites.length === 0" class="empty-state">
        <StarIcon :size="64" :filled="false" />
        <h2>No favourites yet</h2>
        <p>Start adding courses and study programs to your favourites to see them here.</p>
        <router-link to="/courses" class="base-button">
          Browse Courses
        </router-link>
      </div>

      <!-- Favourites Lists -->
      <div v-else>
        <!-- Courses Section -->
        <div v-if="favourites.length > 0">
          <h2 class="section-title">Courses ({{ favourites.length }})</h2>
          <div class="favourites-list">
        <div
          v-for="favourite in sortedFavourites"
          :key="favourite.courseId"
          class="favourite-card"
        >
          <div class="favourite-card__content">
            <div class="favourite-card__header">
              <router-link
                :to="`/courses/${favourite.courseId}`"
                class="favourite-card__title"
              >
                {{ favourite.courseTitle }}
              </router-link>
              <span v-if="favourite.courseEcts" class="favourite-card__ects">
                {{ favourite.courseEcts }} ECTS
              </span>
            </div>

            <p v-if="favourite.courseDescription" class="favourite-card__description">
              {{ favourite.courseDescription }}
            </p>

            <div class="favourite-card__meta">
              <span class="favourite-card__date">
                Added {{ formatDate(favourite.createdAt) }}
              </span>
            </div>
          </div>

          <div class="favourite-card__actions">
            <button
              class="remove-button"
              @click="removeFavourite(favourite.courseId)"
              title="Remove from favourites"
            >
              <StarIcon :size="20" :filled="true" />
            </button>
          </div>
        </div>
          </div>
        </div>

        <!-- Study Programs Section -->
        <div v-if="studyProgramFavourites.length > 0">
          <h2 class="section-title">Study Programs ({{ studyProgramFavourites.length }})</h2>
          <div class="favourites-list">
            <div
              v-for="favourite in sortedStudyProgramFavourites"
              :key="favourite.studyProgramId"
              class="favourite-card"
            >
              <div class="favourite-card__content">
                <div class="favourite-card__header">
                  <router-link
                    :to="`/programs/${favourite.studyProgramId}`"
                    class="favourite-card__title"
                  >
                    {{ favourite.studyProgramName }}
                  </router-link>
                  <span v-if="favourite.totalEcts" class="favourite-card__ects">
                    {{ favourite.totalEcts }} ECTS
                  </span>
                </div>

                <p v-if="favourite.description" class="favourite-card__description">
                  {{ favourite.description }}
                </p>

                <div class="favourite-card__meta">
                  <span v-if="favourite.degree" class="meta-item">{{ favourite.degree }}</span>
                  <span v-if="favourite.semesters" class="meta-item">{{ favourite.semesters }} semesters</span>
                  <span class="favourite-card__date">
                    Added {{ formatDate(favourite.createdAt) }}
                  </span>
                </div>
              </div>

              <div class="favourite-card__actions">
                <button
                  class="remove-button"
                  @click="removeStudyProgramFavourite(favourite.studyProgramId)"
                  title="Remove from favourites"
                >
                  <StarIcon :size="20" :filled="true" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useFavouritesStore } from '../store/favourites.store'
import { useToast } from '@/shared/composables/useToast'
import StarIcon from '@/shared/components/icons/StarIcon.vue'

const favouritesStore = useFavouritesStore()
const toast = useToast()

const favourites = computed(() => favouritesStore.favourites)
const studyProgramFavourites = computed(() => favouritesStore.studyProgramFavourites)
const loading = computed(() => favouritesStore.loading && !favouritesStore.loaded)

const totalCount = computed(() => favourites.value.length + studyProgramFavourites.value.length)

const sortedFavourites = computed(() => {
  return [...favourites.value].sort((a, b) => {
    return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
  })
})

const sortedStudyProgramFavourites = computed(() => {
  return [...studyProgramFavourites.value].sort((a, b) => {
    return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
  })
})

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = Math.abs(now.getTime() - date.getTime())
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

  if (diffDays === 0) return 'today'
  if (diffDays === 1) return 'yesterday'
  if (diffDays < 7) return `${diffDays} days ago`
  if (diffDays < 30) return `${Math.floor(diffDays / 7)} weeks ago`
  if (diffDays < 365) return `${Math.floor(diffDays / 30)} months ago`
  
  return date.toLocaleDateString()
}

async function removeFavourite(courseId: string) {
  try {
    await favouritesStore.removeFavourite(courseId)
    toast.success('Removed from favourites')
  } catch (error: any) {
    toast.error('Failed to remove favourite')
  }
}

async function removeStudyProgramFavourite(studyProgramId: string) {
  try {
    await favouritesStore.removeStudyProgramFavourite(studyProgramId)
    toast.success('Removed from favourites')
  } catch (error: any) {
    toast.error('Failed to remove favourite')
  }
}

onMounted(async () => {
  if (!favouritesStore.loaded) {
    await Promise.all([
      favouritesStore.loadFavourites(),
      favouritesStore.loadStudyProgramFavourites()
    ])
  }
})
</script>

<style scoped>
.list-page {
  background: var(--color-background);
  min-height: 100vh;
}

.page-card {
  max-width: 1000px;
  margin: 0 auto;
  background: var(--color-surface);
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-color, #333);
  margin: 32px 24px 20px 24px;
  padding-bottom: 12px;
  border-bottom: 3px solid var(--primary-color, #3b82f6);
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title:first-of-type {
  margin-top: 24px;
}

.loading-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--text-secondary, #666);
}

.loading-state p {
  font-size: 16px;
  margin: 0;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-state :deep(.star-icon) {
  color: var(--primary-color, #3b82f6);
  margin-bottom: 24px;
  opacity: 0.3;
}

.empty-state h2 {
  margin: 24px 0 12px;
  color: var(--text-color, #333);
  font-size: 28px;
  font-weight: 700;
}

.empty-state p {
  color: var(--text-secondary, #666);
  margin-bottom: 32px;
  font-size: 16px;
}

.favourites-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 20px;
  padding: 0 24px;
  margin-bottom: 24px;
}

.favourite-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: var(--color-surface);
  border: 2px solid var(--color-border);
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.favourite-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color, #3b82f6), var(--star-color, #fbbf24));
  transform: translateX(-100%);
  transition: transform 0.3s ease;
}

.favourite-card:hover::before {
  transform: translateX(0);
}

.favourite-card:hover {
  border-color: var(--primary-color, #3b82f6);
  box-shadow: 0 8px 16px rgba(59, 130, 246, 0.15);
  transform: translateY(-2px);
}

:root[data-theme='dark'] .favourite-card:hover {
  box-shadow: 0 8px 16px rgba(59, 130, 246, 0.25);
}

.favourite-card__content {
  flex: 1;
  min-width: 0;
}

.favourite-card__header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.favourite-card__title {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-color, #3b82f6);
  text-decoration: none;
  transition: all 0.2s;
  flex: 1;
  min-width: 200px;
}

.favourite-card__title:hover {
  color: var(--primary-hover, #2563eb);
  text-decoration: underline;
}

.favourite-card__ects {
  padding: 6px 12px;
  background: linear-gradient(135deg, var(--primary-color, #3b82f6), var(--primary-hover, #2563eb));
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
  color: white;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.2);
}

.favourite-card__description {
  color: var(--text-color, #333);
  margin: 0 0 12px 0;
  line-height: 1.6;
  font-size: 14px;
}

:root[data-theme='dark'] .favourite-card__description {
  color: #d1d5db;
}

.favourite-card__meta {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: var(--text-secondary, #666);
  flex-wrap: wrap;
  align-items: center;
}

.favourite-card__date {
  font-size: 13px;
  color: var(--text-secondary, #666);
  font-weight: 500;
  padding: 4px 8px;
  background: var(--color-surface-alt, #f3f4f6);
  border-radius: 4px;
}

:root[data-theme='dark'] .favourite-card__date {
  background: rgba(59, 130, 246, 0.1);
  color: #93c5fd;
}

.meta-item {
  padding: 6px 12px;
  background: var(--primary-color, #3b82f6);
  opacity: 0.15;
  border: 1px solid var(--primary-color, #3b82f6);
  border-radius: 6px;
  font-size: 12px;
  color: var(--primary-color, #3b82f6);
  font-weight: 600;
}

:root[data-theme='dark'] .meta-item {
  background: rgba(59, 130, 246, 0.2);
  color: #93c5fd;
  border-color: rgba(147, 197, 253, 0.5);
}

.favourite-card__actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex-shrink: 0;
}

.remove-button {
  padding: 10px 12px;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(239, 68, 68, 0.2);
  font-weight: 600;
}

.remove-button:hover {
  background: linear-gradient(135deg, #dc2626, #b91c1c);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.35);
  transform: scale(1.05);
}

.remove-button:active {
  transform: scale(0.98);
}

.remove-button:hover :deep(.star-icon) {
  transform: scale(1.15) rotate(-15deg);
  animation: twinkle 0.4s ease-in-out;
}

@keyframes twinkle {
  0%, 100% { transform: scale(1.15) rotate(-15deg); }
  50% { transform: scale(1.25) rotate(-15deg); }
}

:root[data-theme='dark'] .remove-button {
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}
</style>
