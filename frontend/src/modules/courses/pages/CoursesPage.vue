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
const filterTitle = ref('')
const filterStudyProgram = ref('')
const filterEcts = ref('')
const filterSemester = ref('')
const filterLanguage = ref('')

// Collapsible filters for mobile
const filtersExpanded = ref(false)

// Template refs for input fields
const titleInputRef = ref<HTMLInputElement | null>(null)
const studyProgramInputRef = ref<HTMLInputElement | null>(null)
const ectsInputRef = ref<HTMLInputElement | null>(null)
const semesterInputRef = ref<HTMLInputElement | null>(null)
const languageInputRef = ref<HTMLInputElement | null>(null)

const sortBy = ref<string>('title')
const sortOrder = ref<'asc' | 'desc'>('asc')

// Pagination - Spring uses 0-based page numbers
const currentPage = ref(0)
const pageSize = 50
const totalElements = ref(0)
const totalPages = computed(() => Math.ceil(totalElements.value / pageSize))

const tableColumns = [
  { key: 'title', label: 'Title', thClass: 'sortable', sortable: true },
  { key: 'studyProgram', label: 'Study Program', thClass: 'sortable', sortable: true, sortKey: 'studyProgram.name' },
  { key: 'ects', label: 'ECTS', thClass: 'sortable', sortable: true },
  { key: 'semester', label: 'Semester', thClass: 'sortable', sortable: true },
  { key: 'language', label: 'Language', thClass: 'sortable', sortable: true },
]

const uniqueTitles = computed(() => [...new Set(courses.value.map(c => c.title))].sort())
const uniqueStudyPrograms = computed(() => {
  const programs = new Set<string>()
  courses.value.forEach(c => {
    if (c.studyProgram?.name) {
      const displayName = c.studyProgram.mode 
        ? `${c.studyProgram.name} (${c.studyProgram.mode})`
        : c.studyProgram.name
      programs.add(displayName)
    }
  })
  return [...programs].sort()
})
const uniqueEcts = computed(() => [...new Set(courses.value.map(c => c.ects).filter(v => v !== null && v !== undefined))].sort((a, b) => a - b))
const uniqueSemesters = computed(() => [...new Set(courses.value.map(c => c.semester).filter(v => v !== null && v !== undefined))].sort((a, b) => a - b))
const uniqueLanguages = computed(() => [...new Set(courses.value.map(c => c.language).filter(Boolean))].sort())

// Pagination info for display (convert to 1-based for user)
const startIndex = computed(() => currentPage.value * pageSize + 1)
const endIndex = computed(() => Math.min((currentPage.value + 1) * pageSize, totalElements.value))

function clearAllFilters() {
  filterTitle.value = ''
  filterStudyProgram.value = ''
  filterEcts.value = ''
  filterSemester.value = ''
  filterLanguage.value = ''
  onFiltersChange()
}

function onFiltersChange() {
  currentPage.value = 0
  load()
}

// Watch for sort changes
watch([sortBy, sortOrder], () => {
  currentPage.value = 0
  load()
})

watch([filterTitle, filterStudyProgram, filterEcts, filterSemester, filterLanguage], () => {
  onFiltersChange()
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

function toggleSort(field: string) {
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
  const params: any = {
    page: currentPage.value,
    size: pageSize,
    sort: `${sortBy.value},${sortOrder.value}`
  }
  if (filterTitle.value) params.title = filterTitle.value
  if (filterStudyProgram.value) params.studyProgramName = filterStudyProgram.value
  if (filterEcts.value) params.ects = Number(filterEcts.value)
  if (filterSemester.value) params.semester = Number(filterSemester.value)
  if (filterLanguage.value) params.language = filterLanguage.value
  const response = await coursesApi.getAll(params)
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

      <button 
        class="filters-toggle" 
        :class="{ collapsed: !filtersExpanded }"
        @click="filtersExpanded = !filtersExpanded"
      >
        Filters
      </button>

      <div class="filter-section" :class="{ expanded: filtersExpanded }">
        <div class="filters-row">
          <div class="filter-group">
            <label for="filter-title">Title</label>
            <input
              id="filter-title"
              ref="titleInputRef"
              v-model="filterTitle"
              type="text"
              placeholder="Filter title"
              list="titles-list"
            />
            <datalist id="titles-list">
              <option v-for="title in uniqueTitles" :key="title" :value="title" />
            </datalist>
          </div>

          <div class="filter-group">
            <label for="filter-program">Study Program</label>
            <input
              id="filter-program"
              ref="studyProgramInputRef"
              v-model="filterStudyProgram"
              type="text"
              placeholder="Filter program"
              list="programs-list"
            />
            <datalist id="programs-list">
              <option v-for="prog in uniqueStudyPrograms" :key="prog" :value="prog" />
            </datalist>
          </div>

          <div class="filter-group">
            <label for="filter-ects">ECTS</label>
            <input
              id="filter-ects"
              ref="ectsInputRef"
              v-model="filterEcts"
              type="number"
              min="0"
              placeholder="ECTS"
              list="ects-list"
            />
            <datalist id="ects-list">
              <option v-for="ect in uniqueEcts" :key="ect" :value="ect" />
            </datalist>
          </div>

          <div class="filter-group">
            <label for="filter-semester">Semester</label>
            <input
              id="filter-semester"
              ref="semesterInputRef"
              v-model="filterSemester"
              type="number"
              min="0"
              placeholder="Semester"
              list="semester-list"
            />
            <datalist id="semester-list">
              <option v-for="sem in uniqueSemesters" :key="sem" :value="sem" />
            </datalist>
          </div>

          <div class="filter-group">
            <label for="filter-language">Language</label>
            <input
              id="filter-language"
              ref="languageInputRef"
              v-model="filterLanguage"
              type="text"
              placeholder="Language"
              list="language-list"
            />
            <datalist id="language-list">
              <option v-for="lang in uniqueLanguages" :key="lang" :value="lang" />
            </datalist>
          </div>

          <button class="base-button small clear-filters-btn" @click="clearAllFilters">Clear all filters</button>
        </div>
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
.list-page {
  padding: var(--container-padding);
  display: flex;
  justify-content: center;
  width: 100%;
  box-sizing: border-box;
  min-height: calc(100vh - var(--navbar-height));
}

.page-card {
  width: 100%;
  max-width: 1400px;
  background: var(--color-surface);
  border-radius: var(--radius-md);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-md);
  color: var(--color-text);
  box-sizing: border-box;
}

@media (max-width: 639px) {
  .page-card {
    padding: var(--space-lg);
  }
}

.list-page h1 {
  margin: 0 0 var(--space-lg) 0;
  font-size: var(--font-2xl);
  font-weight: 700;
  color: var(--color-text);
}

.title-with-star {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.course-star-indicator {
  flex-shrink: 0;
  color: var(--star-color);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-sm);
  margin-top: var(--space-2xl);
  padding: var(--space-lg) 0;
  flex-wrap: wrap;
}

.page-numbers {
  display: flex;
  gap: var(--space-xs);
  flex-wrap: wrap;
  justify-content: center;
}

.pagination .base-button {
  min-width: 40px;
  padding: var(--space-sm) var(--space-md);
  font-size: var(--font-sm);
}

.pagination .base-button.active {
  background-color: var(--color-primary);
  color: white;
  font-weight: 700;
}

.pagination .base-button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.results-info {
  color: var(--color-text-muted);
  font-size: var(--font-sm);
  margin-bottom: var(--space-lg);
}

@media (max-width: 639px) {
  .results-info {
    font-size: var(--font-xs);
  }
}
</style>
