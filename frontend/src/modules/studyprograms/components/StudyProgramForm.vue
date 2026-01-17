<script setup lang="ts">
import EntityForm from '@/shared/components/EntityForm.vue'
import { ref, watch } from 'vue'
import { studyProgramsApi } from '../api/studyProgramsApi'
import type { StudyProgram } from '../model/StudyProgram'

const props = defineProps<{
  program: StudyProgram | null
}>()

const emit = defineEmits<{
  saved: []
}>()

const form = ref<StudyProgram>({
  name: '',
  description: '',
  degree: '',
  semesters: undefined,
  mode: '',
  totalEcts: undefined,
  language: 'de',
  applicationPeriod: '',
  startDates: '',
  sourceUrl: '',
})

const loading = ref(false)
const error = ref('')
const success = ref('')

// Watch for prop changes and update form
watch(
  () => props.program,
  (newProgram) => {
    if (newProgram) {
      form.value = { ...newProgram }
    } else {
      resetForm()
    }
  },
  { immediate: true }
)

function resetForm() {
  form.value = {
    name: '',
    description: '',
    degree: '',
    semesters: undefined,
    mode: '',
    totalEcts: undefined,
    language: 'de',
    applicationPeriod: '',
    startDates: '',
    sourceUrl: '',
  }
}

async function handleSubmit(formData: StudyProgram) {
  error.value = ''
  success.value = ''

  if (!formData.name?.trim()) {
    error.value = 'Program name is required'
    return
  }

  loading.value = true
  try {
    if (formData.studyProgramId) {
      // Update
      await studyProgramsApi.update(formData.studyProgramId, formData)
      success.value = 'Program updated successfully'
    } else {
      // Create
      await studyProgramsApi.create(formData)
      success.value = 'Program created successfully'
    }

    setTimeout(() => {
      emit('saved')
      resetForm()
    }, 1000)
  } catch (e: any) {
    error.value = e.response?.data?.message || 'Error saving program'
  } finally {
    loading.value = false
  }
}

function handleCancel() {
  resetForm()
  emit('saved')
}
</script>

<template>
  <EntityForm
    :title="form.studyProgramId ? 'Edit Study Program' : 'Create Study Program'"
    :submitLabel="loading ? 'Saving...' : 'Save'"
    :modelValue="form"
    :errors="error ? { general: error } : {}"
    :success="success"
    :showCancel="true"
    @submit="handleSubmit"
    @cancel="handleCancel"
  >
    <template #fields="{ form }">
      <div class="form-group">
        <label for="name">Program Name *</label>
        <input
          id="name"
          v-model="form.name"
          type="text"
          required
        />
      </div>

      <div class="form-group">
        <label for="description">Description</label>
        <textarea
          id="description"
          v-model="form.description"
          rows="4"
        ></textarea>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="degree">Degree</label>
          <input
            id="degree"
            v-model="form.degree"
            type="text"
          />
        </div>

        <div class="form-group">
          <label for="semesters">Semesters</label>
          <input
            id="semesters"
            v-model.number="form.semesters"
            type="number"
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="mode">Mode</label>
          <input
            id="mode"
            v-model="form.mode"
            type="text"
          />
        </div>

        <div class="form-group">
          <label for="totalEcts">Total ECTS</label>
          <input
            id="totalEcts"
            v-model.number="form.totalEcts"
            type="number"
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="language">Language</label>
          <input
            id="language"
            v-model="form.language"
            type="text"
          />
        </div>

        <div class="form-group">
          <label for="applicationPeriod">Application Period</label>
          <input
            id="applicationPeriod"
            v-model="form.applicationPeriod"
            type="text"
          />
        </div>
      </div>

      <div class="form-group">
        <label for="startDates">Start Dates</label>
        <input
          id="startDates"
          v-model="form.startDates"
          type="text"
        />
      </div>

      <div class="form-group">
        <label for="sourceUrl">Source URL</label>
        <input
          id="sourceUrl"
          v-model="form.sourceUrl"
          type="url"
        />
      </div>
    </template>
  </EntityForm>
</template>

