<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { coursesApi } from '../api/coursesApi';
import type { Course } from '../model/Course';

// --------------------------------------------------
// PROPS
// --------------------------------------------------
const props = defineProps<{
  course: Course | null
}>()

const emit = defineEmits<{
  (e: 'saved'): void
}>()

// --------------------------------------------------
// LOCAL FORM STATE
// --------------------------------------------------
// ❗ ВАЖНО:
// form — это ЛОКАЛЬНОЕ состояние
// мы НИКОГДА не мутируем props.course напрямую
//
const form = reactive<Course>({
  courseId: undefined,
  title: '',
  description: '',
  ects: 0,
  abbreviation: '',
  language: '',
  sws: 0,
  semester: 0,
  kind: '',
  detailsHtml: '',
  sourceUrl: '',
})

const loading = ref(false)
const error = ref('')
const success = ref('')

// --------------------------------------------------
// SYNC PROPS → FORM
// --------------------------------------------------
// Когда:
// - нажали "Edit"
// - или закрыли форму
//
watch(
  () => props.course,
  (course) => {
    if (course) {
      // копируем данные в форму
      Object.assign(form, course)
    } else {
      resetForm()
    }
  },
  { immediate: true }
)

function resetForm() {
  Object.assign(form, {
    courseId: undefined,
    title: '',
    description: '',
    ects: 0,
    abbreviation: '',
    language: '',
    sws: 0,
    semester: 0,
    kind: '',
    detailsHtml: '',
    sourceUrl: '',
  })
}

// --------------------------------------------------
// SAVE
// --------------------------------------------------
async function save() {
  error.value = ''
  success.value = ''

  if (!form.title?.trim()) {
    error.value = 'Course title is required'
    return
  }

  loading.value = true
  try {
    if (form.courseId) {
      // UPDATE
      await coursesApi.update(form.courseId, form)
      success.value = 'Course updated successfully'
    } else {
      // CREATE
      await coursesApi.create(form)
      success.value = 'Course created successfully'
    }

    setTimeout(() => {
      emit('saved')
      resetForm()
    }, 1000)
  } catch (e: any) {
    error.value = e.response?.data?.message || 'Error saving course'
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
    <h2>{{ form.courseId ? 'Edit' : 'Create' }} Course</h2>

    <form @submit.prevent="save" class="course-form">
      <!-- Error Message -->
      <div v-if="error" class="error-message">
        {{ error }}
      </div>

      <!-- Success Message -->
      <div v-if="success" class="success-message">
        {{ success }}
      </div>

      <div class="form-group">
        <label for="title">Course Title *</label>
        <input
          id="title"
          v-model="form.title"
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
          <label for="ects">ECTS *</label>
          <input
            id="ects"
            v-model.number="form.ects"
            type="number"
            min="0"
            step="0.5"
            required
          />
        </div>

        <div class="form-group">
          <label for="sws">SWS (Semester Hours)</label>
          <input
            id="sws"
            v-model.number="form.sws"
            type="number"
            min="0"
            step="0.5"
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="abbreviation">Abbreviation</label>
          <input
            id="abbreviation"
            v-model="form.abbreviation"
            type="text"
          />
        </div>

        <div class="form-group">
          <label for="language">Language</label>
          <input
            id="language"
            v-model="form.language"
            type="text"
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="semester">Semester</label>
          <input
            id="semester"
            v-model.number="form.semester"
            type="number"
            min="0"
          />
        </div>

        <div class="form-group">
          <label for="kind">Kind/Type</label>
          <input
            id="kind"
            v-model="form.kind"
            type="text"
          />
        </div>
      </div>

      <div class="form-group">
        <label for="detailsHtml">Details HTML</label>
        <textarea
          id="detailsHtml"
          v-model="form.detailsHtml"
          rows="4"
          placeholder="Enter HTML content"
        ></textarea>
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
  display: flex;
  flex-direction: column;
  gap: 0;
}

.form-container h2 {
  margin: 0 0 1.5rem 0;
  font-size: 1.5rem;
  color: var(--color-text);
}

.course-form {
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
  font-size: 0.9rem;
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
  background-color: var(--color-primary-hover);
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
  background-color: #fee2e2;
  color: #991b1b;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.success-message {
  padding: 0.75rem;
  background-color: #dcfce7;
  color: #166534;
  border-radius: 4px;
  margin-bottom: 1rem;
}
</style>
