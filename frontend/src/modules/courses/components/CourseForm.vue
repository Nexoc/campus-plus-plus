<script setup lang="ts">
import EntityForm from '@/shared/components/EntityForm.vue';
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
  <EntityForm
    :title="form.courseId ? 'Edit Course' : 'Create Course'"
    :submitLabel="loading ? 'Saving...' : 'Save'"
    :modelValue="form"
    :errors="error ? { general: error } : {}"
    :success="success"
    :showCancel="true"
    @submit="save"
    @cancel="handleCancel"
  >
    <template #fields="{ form }">
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
    </template>
  </EntityForm>
</template>

