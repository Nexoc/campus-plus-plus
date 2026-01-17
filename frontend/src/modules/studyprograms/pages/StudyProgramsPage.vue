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

      <div class="filters-actions">
        <button class="base-button small" @click="clearAllFilters">Clear all filters</button>
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
        <template #th-name>
          <div class="th-cell">
            <div class="th-label">
              Name
              <span v-if="sortBy === 'name'">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
            </div>
            <div class="filter-group">
              <input ref="nameInputRef" v-model="filterName" type="text" placeholder="Filter name" class="th-filter" list="names-list" @click.stop />
              <datalist id="names-list">
                <option v-for="name in uniqueNames" :key="name" :value="name" />
              </datalist>
              <button v-if="filterName" class="clear-filter" @click.stop="filterName=''; onFiltersChange()">×</button>
            </div>
          </div>
        </template>
        <template #th-degree>
          <div class="th-cell">
            <div class="th-label">
              Degree
              <span v-if="sortBy === 'degree'">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
            </div>
            <div class="filter-group">
              <input ref="degreeInputRef" v-model="filterDegree" type="text" placeholder="Degree" class="th-filter" list="degrees-list" @click.stop />
              <datalist id="degrees-list">
                <option v-for="deg in uniqueDegrees" :key="deg" :value="deg" />
              </datalist>
              <button v-if="filterDegree" class="clear-filter" @click.stop="filterDegree=''; onFiltersChange()">×</button>
            </div>
          </div>
        </template>
        <template #th-semesters>
          <div class="th-cell">
            <div class="th-label">
              Semesters
              <span v-if="sortBy === 'semesters'">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
            </div>
            <div class="filter-group">
              <input ref="semestersInputRef" v-model="filterSemesters" type="number" min="0" placeholder="Semesters" class="th-filter th-filter-number" list="semesters-list" @click.stop />
              <datalist id="semesters-list">
                <option v-for="sem in uniqueSemesterCounts" :key="sem" :value="sem" />
              </datalist>
              <button v-if="filterSemesters" class="clear-filter clear-filter-number" @click.stop="filterSemesters=''; onFiltersChange()">×</button>
            </div>
          </div>
        </template>
        <template #th-totalEcts>
          <div class="th-cell">
            <div class="th-label">
              Total ECTS
              <span v-if="sortBy === 'totalEcts'">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
            </div>
            <div class="filter-group">
              <input ref="totalEctsInputRef" v-model="filterTotalEcts" type="number" min="0" placeholder="ECTS" class="th-filter th-filter-number" list="total-ects-list" @click.stop />
              <datalist id="total-ects-list">
                <option v-for="ect in uniqueTotalEcts" :key="ect" :value="ect" />
              </datalist>
              <button v-if="filterTotalEcts" class="clear-filter clear-filter-number" @click.stop="filterTotalEcts=''; onFiltersChange()">×</button>
            </div>
          </div>
        </template>
        <template #th-mode>
          <div class="th-cell">
            <div class="th-label">
              Mode
              <span v-if="sortBy === 'mode'">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
            </div>
            <div class="filter-group">
              <input ref="modeInputRef" v-model="filterMode" type="text" placeholder="Mode" class="th-filter" list="modes-list" @click.stop />
              <datalist id="modes-list">
                <option v-for="m in uniqueModes" :key="m" :value="m" />
              </datalist>
              <button v-if="filterMode" class="clear-filter" @click.stop="filterMode=''; onFiltersChange()">×</button>
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
              <input ref="languageInputRef" v-model="filterLanguage" type="text" placeholder="Language" class="th-filter" list="language-list" @click.stop />
              <datalist id="language-list">
                <option v-for="lang in uniqueLanguages" :key="lang" :value="lang" />
              </datalist>
              <button v-if="filterLanguage" class="clear-filter" @click.stop="filterLanguage=''; onFiltersChange()">×</button>
            </div>
          </div>
        </template>
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
        <button @click="prevPage" :disabled="currentPage === 0" class="page-btn">
          ← Previous
        </button>
        
        <button
          v-for="page in getPaginationRange()"
          :key="page"
          @click="page >= 0 ? goToPage(page) : null"
          :class="['page-btn', { active: page === currentPage, ellipsis: page < 0 }]"
          :disabled="page < 0"
        >
          {{ page < 0 ? '...' : page + 1 }}
        </button>
        
        <button @click="nextPage" :disabled="currentPage >= totalPages - 1" class="page-btn">
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
  gap: 8px;
}

.title-with-star :deep(.star-icon) {
  color: var(--star-color, #fbbf24);
  flex-shrink: 0;
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

.th-filter {
  width: 100%;
  padding: 8px 10px 8px 10px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background: var(--color-surface);
  font-size: 0.9rem;
  cursor: text;
}

.th-filter-number {
  flex: 1;
}

.filter-group {
  position: relative;
  display: flex;
  gap: 4px;
  align-items: center;
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

.filters-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}

.clear-filter {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: none;
  color: var(--color-text-muted);
  cursor: pointer;
  font-size: 18px;
  line-height: 1;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: auto;
}
</style>
