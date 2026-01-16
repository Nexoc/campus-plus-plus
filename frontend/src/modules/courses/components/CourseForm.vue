<script setup lang="ts">
/**
 * CourseForm.vue
 *
 * This form is responsible for creating and editing courses.
 *
 * IMPORTANT DESIGN DECISION:
 * ----------------------------------------
 * - Backend stores course details as ONE HTML string: `detailsHtml`
 * - Editing raw HTML in UI is bad UX
 * - Therefore:
 *   - UI works with structured fields (content, learning outcomes, etc.)
 *   - On LOAD: HTML -> structured fields (parseDetailsHtml)
 *   - On SAVE: structured fields -> HTML (buildDetailsHtml)
 *
 * Backend API and DB schema are NOT changed.
 */

import EntityForm from '@/shared/components/EntityForm.vue'
import { reactive, ref, watch } from 'vue'
import { coursesApi } from '../api/coursesApi'
import type { Course } from '../model/Course'
import { buildDetailsHtml, parseDetailsHtml } from '../util/courseDetailsMapper'

// --------------------------------------------------
// PROPS / EMITS
// --------------------------------------------------
const props = defineProps<{
  course: Course | null
}>()

const emit = defineEmits<{
  (e: 'saved'): void
}>()

// --------------------------------------------------
// FORM-ONLY BLOCK TYPE
// --------------------------------------------------
/**
 * The global RichBlock type is a UNION:
 *   - text block
 *   - list block
 *
 * In THIS FORM we only edit TEXT blocks.
 * Lists are handled as arrays of text blocks.
 *
 * Using a narrower type avoids:
 *   - "property content does not exist" errors
 *   - unsafe v-model bindings in templates
 */
type TextBlock = {
  type: 'text'
  content: string
}

// --------------------------------------------------
// FORM MODEL TYPE
// --------------------------------------------------
/**
 * CourseFormModel is DIFFERENT from Course:
 *
 * - It guarantees that structured fields ALWAYS exist
 * - Fields are NOT optional (unlike in Course)
 * - This makes Vue template bindings type-safe
 */
type CourseFormModel =
  Omit<
    Course,
    | 'content'
    | 'learningOutcomes'
    | 'teachingMethod'
    | 'examMethod'
    | 'literature'
    | 'teachingLanguage'
  > & {
    content: TextBlock[]
    learningOutcomes: TextBlock[]
    teachingMethod: TextBlock[]
    examMethod: TextBlock[]
    literature: TextBlock[]
    teachingLanguage: TextBlock[]
  }

// --------------------------------------------------
// HELPERS
// --------------------------------------------------
/**
 * Factory for creating empty text blocks.
 * Used to guarantee array[0] exists for v-model bindings.
 */
const textBlock = (content = ''): TextBlock => ({
  type: 'text',
  content,
})

// --------------------------------------------------
// LOCAL FORM STATE
// --------------------------------------------------
/**
 * This is LOCAL state.
 * We NEVER mutate props.course directly.
 */
const form = reactive<CourseFormModel>({
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

  // structured fields (always initialized)
  content: [textBlock()],
  learningOutcomes: [],
  teachingMethod: [textBlock()],
  examMethod: [textBlock()],
  literature: [],
  teachingLanguage: [textBlock()],
})

const loading = ref(false)
const error = ref('')
const success = ref('')

// --------------------------------------------------
// RUNTIME SAFETY
// --------------------------------------------------
/**
 * Ensures that all arrays required by the template
 * contain at least one element where needed.
 *
 * This protects against:
 * - partially filled backend data
 * - reset operations
 */
function ensureStructuredFields() {
  if (!form.content.length) form.content.push(textBlock())
  if (!form.teachingMethod.length) form.teachingMethod.push(textBlock())
  if (!form.examMethod.length) form.examMethod.push(textBlock())
  if (!form.teachingLanguage.length) form.teachingLanguage.push(textBlock())
}

// --------------------------------------------------
// SYNC PROPS → FORM
// --------------------------------------------------
/**
 * When:
 * - editing an existing course
 * - switching between courses
 * - closing / reopening the form
 *
 * We:
 * 1) copy backend fields into local form
 * 2) parse detailsHtml into structured fields
 * 3) guarantee required arrays exist
 */
watch(
  () => props.course,
  (course) => {
    if (course) {
      // Copy backend fields
      Object.assign(form, course as CourseFormModel)

      // Convert HTML -> structured fields
      if (course.detailsHtml) {
        Object.assign(
          form,
          parseDetailsHtml(course.detailsHtml) as Partial<CourseFormModel>
        )
      }
    } else {
      resetForm()
    }

    ensureStructuredFields()
  },
  { immediate: true }
)

// --------------------------------------------------
// RESET
// --------------------------------------------------
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
    content: [textBlock()],
    learningOutcomes: [],
    teachingMethod: [textBlock()],
    examMethod: [textBlock()],
    literature: [],
    teachingLanguage: [textBlock()],
  })
}

// --------------------------------------------------
// SAVE
// --------------------------------------------------
async function save() {
  error.value = ''
  success.value = ''

  if (!form.title.trim()) {
    error.value = 'Course title is required'
    return
  }

  /**
   * CRITICAL STEP:
   * Convert structured fields back into HTML
   * BEFORE sending data to backend.
   */
  form.detailsHtml = buildDetailsHtml(form as Course)

  loading.value = true
  try {
    if (form.courseId) {
      await coursesApi.update(form.courseId, form as Course)
      success.value = 'Course updated successfully'
    } else {
      await coursesApi.create(form as Course)
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
    <template #fields>
      <!-- BASIC INFO -->
      <div class="form-group">
        <label>Course Title *</label>
        <input v-model="form.title" type="text" required />
      </div>

      <div class="form-group">
        <label>Description</label>
        <textarea v-model="form.description" rows="3"></textarea>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>ECTS *</label>
          <input v-model.number="form.ects" type="number" min="0" step="0.5" />
        </div>

        <div class="form-group">
          <label>SWS</label>
          <input v-model.number="form.sws" type="number" min="0" step="0.5" />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Language</label>
          <input v-model="form.language" type="text" />
        </div>

        <div class="form-group">
          <label>Semester</label>
          <input v-model.number="form.semester" type="number" min="0" />
        </div>

        <div class="form-group">
          <label>Kind / Type</label>
          <input v-model="form.kind" type="text" />
        </div>
      </div>

      <!-- STRUCTURED DETAILS -->
      <div class="form-group">
        <label>Content</label>
        <textarea v-model="form.content[0].content" rows="4"></textarea>
      </div>

      <div class="form-group">
        <label>Learning Outcomes</label>
        <textarea
          v-for="(b, i) in form.learningOutcomes"
          :key="i"
          v-model="b.content"
          rows="2"
        ></textarea>
        <button
          type="button"
          class="base-button small"
          @click="form.learningOutcomes.push(textBlock())"
        >
          + Add
        </button>
      </div>

      <div class="form-group">
        <label>Teaching Method</label>
        <textarea v-model="form.teachingMethod[0].content" rows="2"></textarea>
      </div>

      <div class="form-group">
        <label>Exam Method</label>
        <textarea v-model="form.examMethod[0].content" rows="2"></textarea>
      </div>

      <div class="form-group">
        <label>Literature</label>
        <textarea
          v-for="(b, i) in form.literature"
          :key="i"
          v-model="b.content"
          rows="2"
        ></textarea>
        <button
          type="button"
          class="base-button small"
          @click="form.literature.push(textBlock())"
        >
          + Add
        </button>
      </div>

      <div class="form-group">
        <label>Teaching Language</label>
        <input v-model="form.teachingLanguage[0].content" type="text" />
      </div>

      <div class="form-group">
        <label>Source URL</label>
        <input v-model="form.sourceUrl" type="url" />
      </div>
    </template>
  </EntityForm>
</template>
