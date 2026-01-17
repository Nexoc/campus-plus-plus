<template>
  <div class="discussions-section">
    <div class="section-header">
      <h2>Discussions</h2>
      <p v-if="!authStore.isAuthenticated" class="login-hint">
        <a href="/login">Log in</a> to participate in discussions
      </p>
    </div>

    <!-- Create Thread Form -->
    <ThreadCreateForm
      v-if="authStore.isAuthenticated"
      :course-id="courseId"
      @thread-created="onThreadCreated"
    />

    <!-- Main View Selector -->
    <div class="view-selector">
      <button
        :class="{ active: view === 'list' }"
        @click="view = 'list'"
        class="btn-view"
      >
        Discussions
      </button>
      <button
        v-if="selectedThreadId"
        :class="{ active: view === 'detail' }"
        @click="view = 'detail'"
        class="btn-view"
      >
        Thread
      </button>
    </div>

    <!-- Thread List View -->
    <div v-show="view === 'list'" class="view-content">
      <ThreadList
        :course-id="courseId"
        :selected-thread-id="selectedThreadId"
        @thread-selected="onThreadSelected"
      />
    </div>

    <!-- Thread Detail View -->
    <div v-show="view === 'detail' && selectedThreadId" class="view-content">
      <button @click="backToList" class="btn-back">← Back to Discussions</button>
      <ThreadDetail :thread-id="selectedThreadId" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/modules/auth/store/auth.store'
import ThreadList from './ThreadList.vue'
import ThreadCreateForm from './ThreadCreateForm.vue'
import ThreadDetail from './ThreadDetail.vue'

interface Props {
  courseId: string
}

const props = defineProps<Props>()

const authStore = useAuthStore()
const view = ref<'list' | 'detail'>('list')
const selectedThreadId = ref<string | null>(null)

function onThreadSelected(threadId: string) {
  console.log('DEBUG DiscussionsSection: onThreadSelected called with:', threadId)
  console.log('DEBUG DiscussionsSection: threadId type:', typeof threadId)
  selectedThreadId.value = threadId
  console.log('DEBUG DiscussionsSection: selectedThreadId.value is now:', selectedThreadId.value)
  view.value = 'detail'
}

function onThreadCreated() {
  view.value = 'list'
  // Thread list will be refreshed automatically via the store
}

function backToList() {
  view.value = 'list'
  selectedThreadId.value = null
}
</script>

<style scoped>
.discussions-section {
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 2px solid #e0e0e0;
}

.section-header {
  margin-bottom: 1.5rem;
}

.section-header h2 {
  margin: 0 0 0.5rem 0;
  color: var(--color-text-primary);
  font-size: 1.5rem;
}

.login-hint {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 0.95rem;
}

.login-hint a {
  color: #007bff;
  text-decoration: none;
  font-weight: 500;
}

.login-hint a:hover {
  text-decoration: underline;
}

.view-selector {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
  border-bottom: 2px solid var(--color-border);
}

.btn-view {
  padding: 0.75rem 1rem;
  background: transparent;
  border: none;
  border-bottom: 3px solid transparent;
  cursor: pointer;
  color: var(--color-text-secondary);
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-view:hover {
  color: var(--color-text-primary);
}

.btn-view.active {
  color: #007bff;
  border-bottom-color: #007bff;
}

.view-content {
  margin-top: 1rem;
}

.btn-back {
  padding: 0.5rem 1rem;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  cursor: pointer;
  color: var(--color-text-primary);
  font-weight: 500;
  transition: all 0.2s ease;
  margin-bottom: 1rem;
}

.btn-back:hover {
  background: var(--color-background);
}
</style>
