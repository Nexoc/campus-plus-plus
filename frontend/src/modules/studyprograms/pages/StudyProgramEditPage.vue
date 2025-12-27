<template>
  <div class="edit-page">
    <div class="form-card">
      <div v-if="loading" class="loading">Loading...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <StudyProgramForm v-else :program="program" @saved="onSaved" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import StudyProgramForm from '@/modules/studyprograms/components/StudyProgramForm.vue'
import { studyProgramsApi } from '@/modules/studyprograms/api/studyProgramsApi'
import type { StudyProgram } from '@/modules/studyprograms/model/StudyProgram'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const error = ref('')
const program = ref<StudyProgram | null>(null)

onMounted(async () => {
  try {
    const id = route.params.id as string
    const res = await studyProgramsApi.getById(id)
    program.value = res.data
  } catch (e: any) {
    error.value = e?.message || 'Failed to load study program'
  } finally {
    loading.value = false
  }
})

function onSaved(updated: StudyProgram) {
  router.push({ name: 'StudyPrograms' })
}
</script>

<style scoped>
.edit-page {
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

.loading,
.error {
  padding: 16px;
  text-align: center;
}

.error {
  color: #b00;
}
</style>
