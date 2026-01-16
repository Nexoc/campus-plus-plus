<template>
  <div class="list-page">
    <div class="page-card">
      <div class="header-row">
        <h1>My Favourites</h1>
        <span v-if="!loading" class="count-badge">
          {{ totalCount }} {{ totalCount === 1 ? 'item' : 'items' }}
        </span>
      </div>

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
                    :to="`/study-programs/${favourite.studyProgramId}`"
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
.count-badge {
  padding: 4px 12px;
  background: var(--primary-color, #3b82f6);
  color: white;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color, #333);
  margin: 30px 0 16px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid var(--border-color, #e0e0e0);
}

.section-title:first-of-type {
  margin-top: 20px;
}

.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-secondary, #666);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-state :deep(.star-icon) {
  color: var(--border-color, #e0e0e0);
  margin-bottom: 20px;
}

.empty-state h2 {
  margin: 20px 0 10px;
  color: var(--text-color, #333);
  font-size: 24px;
}

.empty-state p {
  color: var(--text-secondary, #666);
  margin-bottom: 30px;
}

.favourites-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 20px;
}

.favourite-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: var(--card-bg, #fff);
  border: 1px solid var(--border-color, #e0e0e0);
  border-radius: 8px;
  transition: all 0.2s ease;
}

.favourite-card:hover {
  border-color: var(--star-color, #fbbf24);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.favourite-card__content {
  flex: 1;
  min-width: 0;
}

.favourite-card__header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.favourite-card__title {
  font-size: 18px;
  font-weight: 600;
  color: var(--primary-color, #3b82f6);
  text-decoration: none;
  transition: color 0.2s;
}

.favourite-card__title:hover {
  color: var(--primary-hover, #2563eb);
  text-decoration: underline;
}

.favourite-card__ects {
  padding: 2px 8px;
  background: var(--bg-secondary, #f5f5f5);
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary, #666);
}

.favourite-card__description {
  color: var(--text-color, #333);
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.favourite-card__meta {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: var(--text-secondary, #666);
}

.meta-item {
  padding: 2px 8px;
  background: var(--bg-secondary, #f5f5f5);
  border-radius: 4px;
  font-size: 12px;
}

.favourite-card__actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.remove-button {
  padding: 8px;
  background: none;
  border: 1px solid var(--border-color, #e0e0e0);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--star-color, #fbbf24);
}

.remove-button:hover {
  background: rgba(239, 68, 68, 0.1);
  border-color: #ef4444;
  color: #ef4444;
}

.remove-button:hover :deep(.star-icon) {
  transform: scale(1.1);
}
</style>
