<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { computed, onMounted, ref } from 'vue'
import { coursesApi } from '../api/coursesApi'
import CourseForm from '../components/CourseForm.vue'
import type { Course } from '../model/Course'

const auth = useAuthStore()
const isAdmin = computed(() => auth.isAdmin)

const courses = ref<Course[]>([])
const editing = ref<Course | null>(null)

// -----------------------------
// LOAD COURSES
// -----------------------------
async function load() {
  courses.value = (await coursesApi.getAll()).data
}

// -----------------------------
// EDIT COURSE (IMPORTANT FIX)
// -----------------------------
function editCourse(course: Course) {
  // ❗ create a copy to avoid reactive loop
  editing.value = { ...course }
}

// -----------------------------
// DELETE COURSE
// -----------------------------
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

    <table class="courses-table">
      <thead>
        <tr>
          <th>Title</th>
          <th>ECTS</th>
          <th>Language</th>
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

    <CourseForm
      v-if="isAdmin"
      :course="editing"
      @saved="() => { editing = null; load() }"
    />
  </div>
</template>
