<template>
  <div class="create-page">
    <div class="page-card">
      <CourseForm :course="null" @saved="onSaved" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import CourseForm from '@/modules/courses/components/CourseForm.vue'
import type { Course } from '@/modules/courses/model/Course'

const router = useRouter()

function onSaved(course: Course) {
  router.push({ name: 'Courses' })
}
</script>

