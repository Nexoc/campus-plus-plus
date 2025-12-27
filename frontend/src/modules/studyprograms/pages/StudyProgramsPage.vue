<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { studyProgramsApi } from '../api/studyProgramsApi'
import type { StudyProgram } from '../model/StudyProgram'

const auth = useAuthStore()
const isAdmin = computed(() => auth.isAdmin)

const router = useRouter()

const allPrograms = ref<StudyProgram[]>([])
const searchQuery = ref('')
const sortBy = ref<'name' | 'degree' | 'semesters' | 'totalEcts' | 'mode' | 'language'>('name')
const sortOrder = ref<'asc' | 'desc'>('asc')

// Filtered and sorted programs
const programs = computed(() => {
  let filtered = allPrograms.value.filter(p =>
    p.name?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    p.degree?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    p.mode?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    p.language?.toLowerCase().includes(searchQuery.value.toLowerCase())
  )

  filtered.sort((a, b) => {
    let aVal: any = a[sortBy.value]
    let bVal: any = b[sortBy.value]

    if (aVal === undefined || aVal === null) aVal = ''
    if (bVal === undefined || bVal === null) bVal = ''

    if (typeof aVal === 'string') {
      aVal = aVal.toLowerCase()
      bVal = bVal.toLowerCase()
    }

    const comparison = aVal < bVal ? -1 : aVal > bVal ? 1 : 0
    return sortOrder.value === 'asc' ? comparison : -comparison
  })

  return filtered
})

// Toggle sort
function toggleSort(field: 'name' | 'degree' | 'semesters' | 'totalEcts' | 'mode' | 'language') {
  if (sortBy.value === field) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortBy.value = field
    sortOrder.value = 'asc'
  }
}

// Get sort indicator
function getSortIndicator(field: string) {
  if (sortBy.value !== field) return ''
  return sortOrder.value === 'asc' ? ' ↑' : ' ↓'
}

// Load programs
async function load() {
  allPrograms.value = (await studyProgramsApi.getAll()).data
}

// Edit program (navigate to edit page)
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
  <div class="programs-page">
    <div class="header-row">
      <h1>Study Programs</h1>
      <button v-if="isAdmin" class="base-button small" @click="goCreate">
        + Add Program
      </button>
    </div>

    <!-- Search and Filter Bar -->
    <div class="search-bar">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Search programs..."
        class="search-input"
      />
    </div>

    <!-- Results Count -->
    <div class="results-info">
      Showing {{ programs.length }} of {{ allPrograms.length }} programs
    </div>

    <!-- Table -->
    <table class="programs-table">
      <thead>
        <tr>
          <th
            class="sortable"
            @click="toggleSort('name')"
          >
            Name{{ getSortIndicator('name') }}
          </th>
          <th
            class="sortable"
            @click="toggleSort('degree')"
          >
            Degree{{ getSortIndicator('degree') }}
          </th>
          <th
            class="sortable"
            @click="toggleSort('semesters')"
          >
            Semesters{{ getSortIndicator('semesters') }}
          </th>
          <th
            class="sortable"
            @click="toggleSort('totalEcts')"
          >
            Total ECTS{{ getSortIndicator('totalEcts') }}
          </th>
          <th
            class="sortable"
            @click="toggleSort('mode')"
          >
            Mode{{ getSortIndicator('mode') }}
          </th>
          <th
            class="sortable"
            @click="toggleSort('language')"
          >
            Language{{ getSortIndicator('language') }}
          </th>
          <th v-if="isAdmin" class="actions-col">Actions</th>
        </tr>
      </thead>

      <tbody>
        <tr
          v-for="p in programs"
          :key="p.studyProgramId"
          class="programs-row"
        >
          <td class="title-cell">
            <router-link :to="{ name: 'StudyProgramDetail', params: { id: p.studyProgramId, slug: (p.name || '').toLowerCase().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, '') } }">
              {{ p.name }}
            </router-link>
          </td>
          <td>{{ p.degree }}</td>
          <td>{{ p.semesters }}</td>
          <td>{{ p.totalEcts }}</td>
          <td>{{ p.mode }}</td>
          <td>{{ p.language }}</td>

          <td v-if="isAdmin" class="programs-actions actions-col">
            <button class="base-button" @click="editProgram(p)">
              Edit
            </button>

            <button
              class="base-button danger"
              @click="remove(p.studyProgramId!)"
            >
              Delete
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Empty state -->
    <div v-if="programs.length === 0" class="empty-state">
      No programs found
    </div>
  </div>
</template>
