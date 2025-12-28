<template>
  <div class="create-page">
    <div class="page-card">
      <StudyProgramForm :program="null" @saved="onSaved" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import StudyProgramForm from '@/modules/studyprograms/components/StudyProgramForm.vue'
import type { StudyProgram } from '@/modules/studyprograms/model/StudyProgram'

const router = useRouter()

function onSaved(program: StudyProgram) {
  router.push({ name: 'StudyPrograms' })
}
</script>

