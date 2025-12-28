<template>
  <div class="edit-page">
    <div class="page-card">
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

