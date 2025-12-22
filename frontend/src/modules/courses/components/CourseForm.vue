<script setup lang="ts">
import { reactive, watch } from 'vue';
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
})

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
      // reset формы (Create)
      Object.assign(form, {
        courseId: undefined,
        title: '',
        description: '',
        ects: 0,
        abbreviation: '',
        language: '',
      })
    }
  },
  { immediate: true }
)

// --------------------------------------------------
// SAVE
// --------------------------------------------------
async function save() {
  if (form.courseId) {
    // UPDATE
    await coursesApi.update(form.courseId, {
      title: form.title,
      description: form.description,
      ects: form.ects,
      abbreviation: form.abbreviation,
      language: form.language,
    })
  } else {
    // CREATE
    await coursesApi.create({
      title: form.title,
      description: form.description,
      ects: form.ects,
      abbreviation: form.abbreviation,
      language: form.language,
    })
  }

  emit('saved')
}
</script>

<template>
  <div class="course-form">
    <h3>
      {{ form.courseId ? 'Edit course' : 'Create course' }}
    </h3>

    <div class="course-form__fields">
      <input
        v-model="form.title"
        placeholder="Title"
      />

      <input
        v-model.number="form.ects"
        type="number"
        placeholder="ECTS"
        min="1"
      />

      <input
        v-model="form.language"
        placeholder="Language"
      />

      <input
        v-model="form.abbreviation"
        placeholder="Abbreviation"
      />

      <textarea
        v-model="form.description"
        placeholder="Description"
      />
    </div>

    <div class="course-form__actions">
      <button class="base-button" @click="save">
        Save
      </button>
    </div>
  </div>
</template>
