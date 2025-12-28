<template>
  <div class="edit-page">
    <div class="page-card">
      <div v-if="loading" class="loading">Loading...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <CourseForm v-else :course="course" @saved="onSaved" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import CourseForm from '@/modules/courses/components/CourseForm.vue'
import { coursesApi } from '@/modules/courses/api/coursesApi'
import type { Course } from '@/modules/courses/model/Course'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const error = ref('')
const course = ref<Course | null>(null)

onMounted(async () => {
  try {
    const id = route.params.id as string
    const res = await coursesApi.getById(id)
    course.value = res.data
  } catch (e: any) {
    error.value = e?.message || 'Failed to load course'
  } finally {
    loading.value = false
  }
})

function onSaved(updated: Course) {
  router.push({ name: 'Courses' })
}
</script>

