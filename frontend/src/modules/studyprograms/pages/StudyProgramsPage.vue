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
const searchQuery = ref('')
const sortBy = ref<'name' | 'degree' | 'semesters' | 'totalEcts' | 'mode' | 'language'>('name')
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

function toggleSort(field: 'name' | 'degree' | 'semesters' | 'totalEcts' | 'mode' | 'language') {
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
  const response = await studyProgramsApi.getAll({
    page: currentPage.value,
    size: pageSize,
    sort: `${sortBy.value},${sortOrder.value}`
  })
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

      <div class="search-bar">
        <input
          v-model="searchQuery"
          @input="onSearchChange"
          type="text"
          placeholder="Search programs..."
          class="search-input"
        />
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
</style>
