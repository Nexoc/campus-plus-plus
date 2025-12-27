<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { coursesApi } from '../api/coursesApi'
import type { Course, RichBlock } from '../model/Course'

const route = useRoute()
const course = ref<Course | null>(null)
const loading = ref(false)
const error = ref('')

function normalizeBlocks(raw: unknown): RichBlock[] {
  if (typeof raw === 'string') {
    return [{ type: 'text', content: raw }]
  }

  if (!Array.isArray(raw)) return []

  return raw
    .map((item) => {
      if (typeof item === 'string') {
        return { type: 'text', content: item } as RichBlock
      }
      if (item && typeof item === 'object') {
        const block = item as Record<string, unknown>
        if (block.type === 'text' && typeof block.content === 'string') {
          return { type: 'text', content: block.content } as RichBlock
        }
        if (block.type === 'list' && Array.isArray(block.items)) {
          const items = block.items
            .filter((entry) => typeof entry === 'string')
            .map((entry) => entry)
          return { type: 'list', items, ordered: Boolean(block.ordered) } as RichBlock
        }
      }
      return null
    })
    .filter((b): b is RichBlock => Boolean(b))
}

const sections = computed(() => {
  if (!course.value) return []
  if (course.value.detailsHtml) return []
  return [
    { title: 'Content', blocks: normalizeBlocks(course.value.content) },
    { title: 'Learning Outcomes', blocks: normalizeBlocks(course.value.learningOutcomes) },
    { title: 'Teaching Method', blocks: normalizeBlocks(course.value.teachingMethod) },
    { title: 'Exam Method', blocks: normalizeBlocks(course.value.examMethod) },
    { title: 'Literature', blocks: normalizeBlocks(course.value.literature) },
    { title: 'Teaching Language', blocks: normalizeBlocks(course.value.teachingLanguage) },
  ].filter((section) => section.blocks.length > 0)
})

async function load() {
  loading.value = true
  error.value = ''
  try {
    const id = route.params.id as string
    course.value = (await coursesApi.getById(id)).data
  } catch (e: any) {
    error.value = e.response?.data?.message || 'Failed to load course'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="course-detail-page" v-if="course">
    <h1>{{ course.title }}</h1>
    <p class="course-meta">
      <span v-if="course.abbreviation">Abbreviation: {{ course.abbreviation }}</span>
      <span v-if="course.ects"> · ECTS: {{ course.ects }}</span>
      <span v-if="course.language"> · Language: {{ course.language }}</span>
      <span v-if="course.sws"> · SWS: {{ course.sws }}</span>
      <span v-if="course.semester"> · Semester: {{ course.semester }}</span>
      <span v-if="course.kind"> · Type: {{ course.kind }}</span>
    </p>

    <div v-if="course.description" class="course-description">
      {{ course.description }}
    </div>

    <div class="course-section" v-if="course.detailsHtml">
      <h3>Details</h3>
      <div class="html-content" v-html="course.detailsHtml"></div>
    </div>
    <div v-else>
      <div
        class="course-section"
        v-for="section in sections"
        :key="section.title"
      >
        <h3>{{ section.title }}</h3>
        <div
          class="rich-block"
          v-for="(block, idx) in section.blocks"
          :key="`${section.title}-${idx}`"
        >
          <p v-if="block.type === 'text'" v-html="block.content"></p>
          <ul v-else-if="block.type === 'list' && !block.ordered">
            <li v-for="(item, i) in block.items" :key="i" v-html="item"></li>
          </ul>
          <ol v-else-if="block.type === 'list' && block.ordered">
            <li v-for="(item, i) in block.items" :key="i" v-html="item"></li>
          </ol>
        </div>
      </div>
    </div>

    <div class="course-section" v-if="course.sourceUrl">
      <h3>Source</h3>
      <a :href="course.sourceUrl" target="_blank" rel="noreferrer">{{ course.sourceUrl }}</a>
    </div>
  </div>

  <div v-else class="course-detail-page">
    <div v-if="loading">Loading...</div>
    <div v-if="error" class="error-message">{{ error }}</div>
  </div>
</template>

<style scoped>
.course-detail-page {
  max-width: 900px;
  margin: 3rem auto;
  padding: 2rem;
  background: var(--color-surface);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
}

.course-meta {
  color: var(--color-text-muted);
  margin: 0.5rem 0 1.5rem;
}

.course-description {
  white-space: pre-wrap;
}

.course-section {
  margin-top: 1.25rem;
}

.html-content :global(h4) {
  margin: 0.5rem 0 0.25rem;
}

.html-content :global(p) {
  margin: 0.35rem 0;
}

.rich-block {
  margin-top: 0.5rem;
}

.rich-block ul,
.rich-block ol {
  padding-left: 1.1rem;
  margin: 0.25rem 0 0.5rem;
}

.rich-block li + li {
  margin-top: 0.2rem;
}

.error-message {
  color: var(--color-error);
}
</style>
