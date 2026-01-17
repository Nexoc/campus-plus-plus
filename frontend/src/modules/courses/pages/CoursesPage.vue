<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { useFavouritesStore } from '@/modules/favourites/store/favourites.store'
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { coursesApi } from '../api/coursesApi'
import type { Course } from '../model/Course'
import EntityTable from '@/shared/components/EntityTable.vue'
import StarIcon from '@/shared/components/icons/StarIcon.vue'

const auth = useAuthStore()
const favouritesStore = useFavouritesStore()
const isAuthenticated = computed(() => auth.isAuthenticated)

const router = useRouter()

const courses = ref<Course[]>([])
const searchQuery = ref('')
const sortBy = ref<'title' | 'ects' | 'semester' | 'language'>('title')
const sortOrder = ref<'asc' | 'desc'>('asc')

// Pagination - Spring uses 0-based page numbers
const currentPage = ref(0)
const pageSize = 50
const totalElements = ref(0)
const totalPages = computed(() => Math.ceil(totalElements.value / pageSize))

const tableColumns = [
  { key: 'title', label: 'Title', thClass: 'sortable', sortable: true },
  { key: 'studyProgram', label: 'Study Program', thClass: '', sortable: false },
  { key: 'ects', label: 'ECTS', thClass: 'sortable', sortable: true },
  { key: 'semester', label: 'Semester', thClass: 'sortable', sortable: true },
  { key: 'language', label: 'Language', thClass: 'sortable', sortable: true },
]

// Pagination info for display (convert to 1-based for user)
const startIndex = computed(() => currentPage.value * pageSize + 1)
const endIndex = computed(() => Math.min((currentPage.value + 1) * pageSize, totalElements.value))

// Reset to page 0 when search changes
function onSearchChange() {
  currentPage.value = 0
  load()
}

// Watch for sort changes
watch([sortBy, sortOrder], () => {
  currentPage.value = 0
  load()
})

function goToPage(page: number) {
  currentPage.value = page
  load()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function nextPage() {
  if (currentPage.value < totalPages.value - 1) {
    goToPage(currentPage.value + 1)
  }
}

function prevPage() {
  if (currentPage.value > 0) {
    goToPage(currentPage.value - 1)
  }
}

function toggleSort(field: 'title' | 'ects' | 'semester' | 'language') {
  if (sortBy.value === field) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortBy.value = field
    sortOrder.value = 'asc'
  }
}

function getPaginationRange() {
  const range: number[] = []
  const maxVisible = 7
  
  if (totalPages.value <= maxVisible) {
    for (let i = 0; i < totalPages.value; i++) {
      range.push(i)
    }
  } else {
    if (currentPage.value <= 3) {
      for (let i = 0; i < 5; i++) range.push(i)
      range.push(-1)
      range.push(totalPages.value - 1)
    } else if (currentPage.value >= totalPages.value - 4) {
      range.push(0)
      range.push(-1)
      for (let i = totalPages.value - 5; i < totalPages.value; i++) range.push(i)
    } else {
      range.push(0)
      range.push(-1)
      for (let i = currentPage.value - 1; i <= currentPage.value + 1; i++) range.push(i)
      range.push(-1)
      range.push(totalPages.value - 1)
    }
  }
  
  return range
}

async function load() {
  const response = await coursesApi.getAll({
    page: currentPage.value,
    size: pageSize,
    sort: `${sortBy.value},${sortOrder.value}`
  })
  courses.value = response.data.content
  totalElements.value = response.data.totalElements
  
  if (isAuthenticated.value) {
    await favouritesStore.loadFavourites()
  }
}

function editCourse(course: Course) {
  router.push({ name: 'CourseEdit', params: { id: course.courseId } })
}

function asCourse(row: Record<string, any>): Course {
  return row as Course
}

async function remove(courseId: string) {
  if (!confirm('Delete course?')) return
  await coursesApi.remove(courseId)
  await load()
}

function goCreate() {
  router.push({ name: 'CourseCreate' })
}

onMounted(load)
</script>

<template>
  <div class="list-page">
    <div class="page-card">
      <div class="header-row">
        <h1>Courses</h1>
        <button v-if="auth.isModerator" class="base-button small" @click="goCreate">
          + Add Course
        </button>
      </div>

      <div class="search-bar">
        <input
          v-model="searchQuery"
          @input="onSearchChange"
          type="text"
          placeholder="Search courses..."
          class="search-input"
        />
      </div>

      <div class="results-info">
        Showing {{ startIndex }}-{{ endIndex }} of {{ totalElements }} courses
      </div>

      <EntityTable
        :columns="tableColumns"
        :rows="courses"
        rowKey="courseId"
        :hasActions="auth.isModerator"
        :sortBy="sortBy"
        :sortOrder="sortOrder"
        @sort="toggleSort"
      >
        <template #title="{ row }">
          <div class="title-with-star">
            <StarIcon v-if="isAuthenticated && favouritesStore.isFavourite(row.courseId)" :size="16" :filled="true" class="course-star-indicator" />
            <router-link :to="{ name: 'CourseDetail', params: { id: row.courseId, slug: (row.title || '').toLowerCase().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, '') } }">
              {{ row.title }}
            </router-link>
          </div>
        </template>
        <template #studyProgram="{ row }">
          <router-link
            v-if="row.studyProgram"
            :to="{ name: 'StudyProgramDetail', params: { id: row.studyProgram.id } }"
          >
            {{ row.studyProgram.name }}<span v-if="row.studyProgram.mode"> ({{ row.studyProgram.mode }})</span>
          </router-link>
          <span v-else class="text-muted">—</span>
        </template>
        <template #actions="{ row }">
          <button class="base-button" @click="editCourse(asCourse(row))">Edit</button>
          <button class="base-button danger" @click="remove(row.courseId)">Delete</button>
        </template>
      </EntityTable>

      <div v-if="courses.length === 0" class="empty-state">
        No courses found
      </div>

      <div v-if="totalPages > 1" class="pagination">
        <button 
          class="base-button small" 
          @click="prevPage" 
          :disabled="currentPage === 0"
        >
          ← Previous
        </button>
        
        <div class="page-numbers">
          <button
            v-for="page in getPaginationRange()"
            :key="page"
            class="base-button small"
            :class="{ active: page === currentPage }"
            @click="page >= 0 ? goToPage(page) : null"
            :disabled="page === -1"
          >
            {{ page >= 0 ? page + 1 : '...' }}
          </button>
        </div>
        
        <button 
          class="base-button small" 
          @click="nextPage" 
          :disabled="currentPage === totalPages - 1"
        >
          Next →
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.title-with-star {
  display: flex;
  align-items: center;
  gap: 6px;
}

.course-star-indicator {
  flex-shrink: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 24px;
  padding: 16px 0;
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.pagination .base-button {
  min-width: 40px;
}

.pagination .base-button.active {
  background-color: var(--color-primary);
  color: white;
  font-weight: 600;
}

.pagination .base-button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.results-info {
  color: var(--color-text-muted);
  font-size: 0.9rem;
  margin-bottom: 12px;
}
</style>
