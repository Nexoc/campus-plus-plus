<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import EntityTable from '@/shared/components/EntityTable.vue'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { coursesApi } from '../api/coursesApi'
import type { Course } from '../model/Course'

const auth = useAuthStore()

const router = useRouter()

const allCourses = ref<Course[]>([])
const searchQuery = ref('')
const sortBy = ref<'title' | 'ects' | 'semester' | 'language'>('title')
const sortOrder = ref<'asc' | 'desc'>('asc')

const tableColumns = [
  { key: 'title', label: 'Title', thClass: 'sortable', sortable: true },
  { key: 'studyProgram', label: 'Study Program', thClass: '', sortable: false },
  { key: 'ects', label: 'ECTS', thClass: 'sortable', sortable: true },
  { key: 'semester', label: 'Semester', thClass: 'sortable', sortable: true },
  { key: 'language', label: 'Language', thClass: 'sortable', sortable: true },
]

// Filtered and sorted courses
const courses = computed(() => {
  let filtered = allCourses.value.filter(c =>
    c.title?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    c.language?.toLowerCase().includes(searchQuery.value.toLowerCase())
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

function toggleSort(field: 'title' | 'ects' | 'semester' | 'language') {
  if (sortBy.value === field) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortBy.value = field
    sortOrder.value = 'asc'
  }
}
async function load() {
  const response = await coursesApi.getAll()
  allCourses.value = response.data
}

function editCourse(row: any) {
  const course = row as Course
  router.push({ name: 'CourseEdit', params: { id: course.courseId } })
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

      <!-- Search and Filter Bar -->
      <div class="search-bar">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Search courses..."
          class="search-input"
        />
      </div>

      <!-- Results Count -->
      <div class="results-info">
        Showing {{ courses.length }} of {{ allCourses.length }} courses
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
          <router-link :to="{ name: 'CourseDetail', params: { id: row.courseId, slug: (row.title || '').toLowerCase().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, '') } }">
            {{ row.title }}
          </router-link>
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
          <button class="base-button" @click="editCourse(row)">Edit</button>
          <button class="base-button danger" @click="remove(row.courseId)">Delete</button>
        </template>
      </EntityTable>

      <!-- Empty state -->
      <div v-if="courses.length === 0" class="empty-state">
        No courses found
      </div>
    </div>
  </div>
</template>
