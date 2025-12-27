<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { computed, onMounted, ref } from 'vue'
import { coursesApi } from '../api/coursesApi'
import CourseForm from '../components/CourseForm.vue'
import type { Course } from '../model/Course'

const auth = useAuthStore()
const isAdmin = computed(() => auth.isAdmin)

const allCourses = ref<Course[]>([])
const editing = ref<Course | null>(null)
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

// Edit course
function editCourse(course: Course) {
  editing.value = { ...course }
}

// Delete course
async function remove(courseId: string) {
  if (!confirm('Delete course?')) return
  await coursesApi.remove(courseId)
  await load()
}

onMounted(load)
</script>

<template>
  <div class="courses-page">
    <h1>Courses</h1>

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
          <th v-if="isAdmin">Actions</th>
        </tr>
      </thead>

      <tbody>
        <tr
          v-for="c in courses"
          :key="c.courseId"
          class="courses-row"
        >
          <td>{{ c.title }}</td>
          <td>{{ c.ects }}</td>
          <td>{{ c.language }}</td>

          <td v-if="isAdmin" class="courses-actions">
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

    <!-- Course Form -->
    <CourseForm
      v-if="isAdmin"
      :course="editing"
      @saved="() => { editing = null; load() }"
    />
  </div>
</template>
