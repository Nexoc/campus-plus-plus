<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { useFavouritesStore } from '@/modules/favourites/store/favourites.store'
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { studyProgramsApi } from '../api/studyProgramsApi'
import type { StudyProgram } from '../model/StudyProgram'
import EntityTable from '@/shared/components/EntityTable.vue'
import StarIcon from '@/shared/components/icons/StarIcon.vue'

const auth = useAuthStore()
const favouritesStore = useFavouritesStore()
const isAuthenticated = computed(() => auth.isAuthenticated)

const router = useRouter()

const programs = ref<StudyProgram[]>([])
const filterName = ref('')
const filterDegree = ref('')
const filterSemesters = ref('')
const filterTotalEcts = ref('')
const filterMode = ref('')
const filterLanguage = ref('')

// Collapsible filters for mobile
const filtersExpanded = ref(false)

// Template refs for input fields
const nameInputRef = ref<HTMLInputElement | null>(null)
const degreeInputRef = ref<HTMLInputElement | null>(null)
const semestersInputRef = ref<HTMLInputElement | null>(null)
const totalEctsInputRef = ref<HTMLInputElement | null>(null)
const modeInputRef = ref<HTMLInputElement | null>(null)
const languageInputRef = ref<HTMLInputElement | null>(null)

const sortBy = ref<string>('name')
const sortOrder = ref<'asc' | 'desc'>('asc')

// Pagination - Spring uses 0-based page numbers
const currentPage = ref(0)
const pageSize = 50
const totalElements = ref(0)
const totalPages = computed(() => Math.ceil(totalElements.value / pageSize))

const tableColumns = [
  { key: 'name', label: 'Name', thClass: 'sortable', sortable: true },
  { key: 'degree', label: 'Degree', thClass: 'sortable', sortable: true },
  { key: 'semesters', label: 'Semesters', thClass: 'sortable', sortable: true },
  { key: 'totalEcts', label: 'Total ECTS', thClass: 'sortable', sortable: true },
  { key: 'mode', label: 'Mode', thClass: 'sortable', sortable: true },
  { key: 'language', label: 'Language', thClass: 'sortable', sortable: true },
]

const uniqueNames = computed(() => [...new Set(programs.value.map(p => p.name))].sort())
const uniqueDegrees = computed(() => [...new Set(programs.value.map(p => p.degree).filter(Boolean))].sort())
const uniqueSemesterCounts = computed(() => [...new Set(programs.value.map(p => p.semesters).filter(v => v !== null && v !== undefined))].sort((a, b) => a - b))
const uniqueTotalEcts = computed(() => [...new Set(programs.value.map(p => p.totalEcts).filter(v => v !== null && v !== undefined))].sort((a, b) => a - b))
const uniqueModes = computed(() => [...new Set(programs.value.map(p => p.mode).filter(Boolean))].sort())
const uniqueLanguages = computed(() => [...new Set(programs.value.map(p => p.language).filter(Boolean))].sort())

// Pagination info for display (convert to 1-based for user)
const startIndex = computed(() => currentPage.value * pageSize + 1)
const endIndex = computed(() => Math.min((currentPage.value + 1) * pageSize, totalElements.value))

function onFiltersChange() {
  currentPage.value = 0
  load()
}


// Watch for sort changes
watch([sortBy, sortOrder], () => {
  currentPage.value = 0
  load()
})

watch([filterName, filterDegree, filterSemesters, filterTotalEcts, filterMode, filterLanguage], () => {
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

function clearAllFilters() {
  filterName.value = ''
  filterDegree.value = ''
  filterSemesters.value = ''
  filterTotalEcts.value = ''
  filterMode.value = ''
  filterLanguage.value = ''
  onFiltersChange()
}

function toggleSort(field: 'name' | 'degree' | 'semesters' | 'totalEcts' | 'mode' | 'language') {
  if (sortBy.value === field) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortBy.value = field
    sortOrder.value = 'asc'
  }
}

function toggleSortAny(field: string) {
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
  if (filterName.value) params.name = filterName.value
  if (filterDegree.value) params.degree = filterDegree.value
  if (filterSemesters.value) params.semesters = Number(filterSemesters.value)
  if (filterTotalEcts.value) params.totalEcts = Number(filterTotalEcts.value)
  if (filterMode.value) params.mode = filterMode.value
  if (filterLanguage.value) params.language = filterLanguage.value
  const response = await studyProgramsApi.getAll(params)
  programs.value = response.data.content
  totalElements.value = response.data.totalElements
  
  if (isAuthenticated.value) {
    await favouritesStore.loadStudyProgramFavourites()
  }
}
function editProgram(program: StudyProgram) {
  router.push({ name: 'StudyProgramEdit', params: { id: program.studyProgramId } })
}

// Delete program
async function remove(programId: string) {
  if (!confirm('Delete study program?')) return
  await studyProgramsApi.remove(programId)
  await load()
}

// Create program (navigate to create page)
function goCreate() {
  router.push({ name: 'StudyProgramCreate' })
}

onMounted(load)
</script>

<template>
  <div class="list-page">
    <div class="page-card">
      <div class="header-row">
        <h1>Study Programs</h1>
        <button v-if="auth.isModerator" class="base-button small" @click="goCreate">
          + Add Program
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
            <label for="filter-name">Name</label>
            <input
              id="filter-name"
              ref="nameInputRef"
              v-model="filterName"
              type="text"
              placeholder="Filter name"
              list="names-list"
            />
            <datalist id="names-list">
              <option v-for="name in uniqueNames" :key="name" :value="name" />
            </datalist>
          </div>

          <div class="filter-group">
            <label for="filter-degree">Degree</label>
            <input
              id="filter-degree"
              ref="degreeInputRef"
              v-model="filterDegree"
              type="text"
              placeholder="Filter degree"
              list="degrees-list"
            />
            <datalist id="degrees-list">
              <option v-for="deg in uniqueDegrees" :key="deg" :value="deg" />
            </datalist>
          </div>

          <div class="filter-group">
            <label for="filter-semesters">Semesters</label>
            <input
              id="filter-semesters"
              ref="semestersInputRef"
              v-model="filterSemesters"
              type="number"
              min="0"
              placeholder="Semesters"
              list="semesters-list"
            />
            <datalist id="semesters-list">
              <option v-for="sem in uniqueSemesterCounts" :key="sem" :value="sem" />
            </datalist>
          </div>

          <div class="filter-group">
            <label for="filter-total-ects">Total ECTS</label>
            <input
              id="filter-total-ects"
              ref="totalEctsInputRef"
              v-model="filterTotalEcts"
              type="number"
              min="0"
              placeholder="Total ECTS"
              list="total-ects-list"
            />
            <datalist id="total-ects-list">
              <option v-for="ect in uniqueTotalEcts" :key="ect" :value="ect" />
            </datalist>
          </div>

          <div class="filter-group">
            <label for="filter-mode">Mode</label>
            <input
              id="filter-mode"
              ref="modeInputRef"
              v-model="filterMode"
              type="text"
              placeholder="Mode"
              list="modes-list"
            />
            <datalist id="modes-list">
              <option v-for="m in uniqueModes" :key="m" :value="m" />
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
        Showing {{ startIndex }} - {{ endIndex }} of {{ totalElements }} programs
      </div>

      <EntityTable
        :columns="tableColumns"
        :rows="programs"
        rowKey="studyProgramId"
        :hasActions="auth.isModerator"
        :sortBy="sortBy"
        :sortOrder="sortOrder"
        @sort="toggleSort"
      >
        <template #name="{ row }">
          <div class="title-with-star">
            <StarIcon 
              v-if="isAuthenticated && favouritesStore.isStudyProgramFavourite(row.studyProgramId)" 
              :size="16" 
              :filled="true" 
            />
            <router-link :to="{ name: 'StudyProgramDetail', params: { id: row.studyProgramId, slug: (row.name || '').toLowerCase().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, '') } }">
              {{ row.name }}
            </router-link>
          </div>
        </template>
        <template #actions="{ row }">
          <button class="base-button" @click="editProgram(row)">Edit</button>
          <button class="base-button danger" @click="remove(row.studyProgramId)">Delete</button>
        </template>
      </EntityTable>

      <div v-if="programs.length === 0" class="empty-state">
        No programs found
      </div>

      <!-- Pagination Controls -->
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
          :disabled="currentPage >= totalPages - 1"
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

.title-with-star :deep(.star-icon) {
  color: var(--star-color);
  flex-shrink: 0;
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
