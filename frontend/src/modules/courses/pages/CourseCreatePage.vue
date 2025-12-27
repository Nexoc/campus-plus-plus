<template>
  <div class="create-page">
    <div class="form-card">
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

<style scoped>
.create-page {
  padding: 24px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  background: var(--color-background);
}

.form-card {
  background: white;
  border-radius: 8px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 100%;
}

.form-card h1 {
  margin-top: 0;
  margin-bottom: 24px;
  color: var(--color-text);
}
</style>
