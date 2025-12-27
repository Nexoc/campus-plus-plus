<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { coursesApi } from '../api/coursesApi'
import type { Course } from '../model/Course'

const auth = useAuthStore()
const isAdmin = computed(() => auth.isAdmin)

const router = useRouter()

const allCourses = ref<Course[]>([])
const searchQuery = ref('')
const sortBy = ref<'title' | 'ects' | 'language'>('title')
const sortOrder = ref<'asc' | 'desc'>('asc')

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

// Toggle sort
function toggleSort(field: 'title' | 'ects' | 'language') {
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

// Load courses
async function load() {
  allCourses.value = (await coursesApi.getAll()).data
}

// Edit course (navigate to edit page)
function editCourse(course: Course) {
  router.push({ name: 'CourseEdit', params: { id: course.courseId } })
}

// Delete course
async function remove(courseId: string) {
  if (!confirm('Delete course?')) return
  await coursesApi.remove(courseId)
  await load()
}

// Create course (navigate to dedicated page)
function goCreate() {
  router.push({ name: 'CourseCreate' })
}

onMounted(load)
</script>

<template>
  <div class="courses-page">
    <div class="header-row">
      <h1>Courses</h1>
      <button v-if="isAdmin" class="base-button small" @click="goCreate">
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

    <!-- Table -->
    <table class="courses-table">
      <thead>
        <tr>
          <th
            class="sortable"
            @click="toggleSort('title')"
          >
            Title{{ getSortIndicator('title') }}
          </th>
          <th
            class="sortable"
            @click="toggleSort('ects')"
          >
            ECTS{{ getSortIndicator('ects') }}
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
          v-for="c in courses"
          :key="c.courseId"
          class="courses-row"
        >
          <td class="title-cell">
            <router-link :to="{ name: 'CourseDetail', params: { id: c.courseId, slug: (c.title || '').toLowerCase().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, '') } }">
              {{ c.title }}
            </router-link>
          </td>
          <td>{{ c.ects }}</td>
          <td>{{ c.language }}</td>

          <td v-if="isAdmin" class="courses-actions actions-col">
            <button class="base-button" @click="editCourse(c)">
              Edit
            </button>

            <button
              class="base-button danger"
              @click="remove(c.courseId!)"
            >
              Delete
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Empty state -->
    <div v-if="courses.length === 0" class="empty-state">
      No courses found
    </div>
  </div>
</template>
