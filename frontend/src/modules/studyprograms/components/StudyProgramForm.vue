<script setup lang="ts">
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

async function handleSubmit() {
  error.value = ''
  success.value = ''

  if (!form.value.name?.trim()) {
    error.value = 'Program name is required'
    return
  }

  loading.value = true
  try {
    if (form.value.studyProgramId) {
      // Update
      await studyProgramsApi.update(form.value.studyProgramId, form.value)
      success.value = 'Program updated successfully'
    } else {
      // Create
      await studyProgramsApi.create(form.value)
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
  <div class="form-container">
    <h2>{{ form.studyProgramId ? 'Edit' : 'Create' }} Study Program</h2>

    <form @submit.prevent="handleSubmit" class="study-program-form">
      <!-- Error Message -->
      <div v-if="error" class="error-message">
        {{ error }}
      </div>

      <!-- Success Message -->
      <div v-if="success" class="success-message">
        {{ success }}
      </div>

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

      <div class="form-actions">
        <button
          type="submit"
          class="base-button"
          :disabled="loading"
        >
          {{ loading ? 'Saving...' : 'Save' }}
        </button>

        <button
          type="button"
          class="base-button secondary"
          @click="handleCancel"
          :disabled="loading"
        >
          Cancel
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.form-container {
  margin-top: 2rem;
  padding: 2rem;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background-color: var(--color-background-alt);
}

.form-container h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
}

.study-program-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 500;
  color: var(--color-text);
}

.form-group input,
.form-group textarea {
  padding: 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background-color: var(--color-surface);
  color: var(--color-text);
  font-family: inherit;
  font-size: 0.95rem;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.base-button {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  background-color: var(--color-primary);
  color: white;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.base-button:hover:not(:disabled) {
  background-color: var(--color-primary-dark);
}

.base-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.base-button.secondary {
  background-color: var(--color-border);
  color: var(--color-text);
}

.base-button.secondary:hover:not(:disabled) {
  background-color: var(--color-text-muted);
}

.error-message {
  padding: 0.75rem;
  background-color: var(--color-error-bg);
  color: var(--color-error);
  border-radius: 4px;
  margin-bottom: 1rem;
}

.success-message {
  padding: 0.75rem;
  background-color: var(--color-success-bg);
  color: var(--color-success);
  border-radius: 4px;
  margin-bottom: 1rem;
}
</style>
