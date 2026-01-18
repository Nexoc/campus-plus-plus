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

      <div class="filters-actions">
        <button class="base-button small" @click="clearAllFilters">Clear all filters</button>
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
        <template #th-title>
          <div class="th-cell">
            <div class="th-label">
              Title
              <span v-if="sortBy === 'title'">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
            </div>
            <div class="filter-group">
              <input
                ref="titleInputRef"
                v-model="filterTitle"
                type="text"
                placeholder="Filter title"
                class="th-filter"
                list="titles-list"
                @click.stop
              />
              <datalist id="titles-list">
                <option v-for="title in uniqueTitles" :key="title" :value="title" />
              </datalist>
              <button v-if="filterTitle" class="clear-filter" @click.stop="filterTitle=''; onFiltersChange()">×</button>
            </div>
          </div>
        </template>
        <template #th-studyProgram>
          <div class="th-cell">
            <div class="th-label">
              Study Program
              <span v-if="sortBy === 'studyProgram.name'">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
            </div>
            <div class="filter-group">
              <input
                ref="studyProgramInputRef"
                v-model="filterStudyProgram"
                type="text"
                placeholder="Filter program"
                class="th-filter"
                list="programs-list"
                @click.stop
              />
              <datalist id="programs-list">
                <option v-for="prog in uniqueStudyPrograms" :key="prog" :value="prog" />
              </datalist>
              <button v-if="filterStudyProgram" class="clear-filter" @click.stop="filterStudyProgram=''; onFiltersChange()">×</button>
            </div>
          </div>
        </template>
        <template #th-ects>
          <div class="th-cell">
            <div class="th-label">
              ECTS
              <span v-if="sortBy === 'ects'">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
            </div>
            <div class="filter-group">
              <input
                ref="ectsInputRef"
                v-model="filterEcts"
                type="number"
                min="0"
                placeholder="ECTS"
                class="th-filter th-filter-number"
                list="ects-list"
                @click.stop
              />
              <datalist id="ects-list">
                <option v-for="ect in uniqueEcts" :key="ect" :value="ect" />
              </datalist>
              <button v-if="filterEcts" class="clear-filter clear-filter-number" @click.stop="filterEcts=''; onFiltersChange()">×</button>
            </div>
          </div>
        </template>
        <template #th-semester>
          <div class="th-cell">
            <div class="th-label">
              Semester
              <span v-if="sortBy === 'semester'">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
            </div>
            <div class="filter-group">
              <input
                ref="semesterInputRef"
                v-model="filterSemester"
                type="number"
                min="0"
                placeholder="Semester"
                class="th-filter th-filter-number"
                list="semester-list"
                @click.stop
              />
              <datalist id="semester-list">
                <option v-for="sem in uniqueSemesters" :key="sem" :value="sem" />
              </datalist>
              <button v-if="filterSemester" class="clear-filter clear-filter-number" @click.stop="filterSemester=''; onFiltersChange()">×</button>
            </div>
          </div>
        </template>
        <template #th-language>
          <div class="th-cell">
            <div class="th-label">
              Language
              <span v-if="sortBy === 'language'">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
            </div>
            <div class="filter-group">
              <input
                ref="languageInputRef"
                v-model="filterLanguage"
                type="text"
                placeholder="Language"
                class="th-filter"
                list="language-list"
                @click.stop
              />
              <datalist id="language-list">
                <option v-for="lang in uniqueLanguages" :key="lang" :value="lang" />
              </datalist>
              <button v-if="filterLanguage" class="clear-filter" @click.stop="filterLanguage=''; onFiltersChange()">×</button>
            </div>
          </div>
        </template>
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
.filters-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}
.th-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.th-label {
  display: inline-flex;
  gap: 6px;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.filter-group {
  position: relative;
  display: flex;
  gap: 4px;
  align-items: center;
}

.th-filter {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background: var(--color-surface);
  color: var(--color-text-primary);
  font-size: 0.9rem;
  cursor: text;
}

/* Remove extra padding for number fields since controls are inside */
.th-filter-number {
  flex: 1;
}

.clear-filter {
  background: transparent;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  padding: 6px 8px;
  color: var(--color-text-muted);
  cursor: pointer;
  font-size: 18px;
  height: 32px;
  width: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.clear-filter:hover {
  color: var(--color-text);
}

.clear-filter-number {
  /* No extra positioning needed for flex layout */
}
</style>
