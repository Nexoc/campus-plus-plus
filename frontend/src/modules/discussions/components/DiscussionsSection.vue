<template>
  <CollapsibleSection
    title="Discussions"
    :show-toggle="authStore.isAuthenticated && view === 'list'"
    show-label="Create New Thread"
    hide-label="Hide Form"
  >
    <template #header-extra>
      <p v-if="!authStore.isAuthenticated" class="login-hint">
        <a href="/login">Log in</a> to participate
      </p>
    </template>

    <template #form>
      <ThreadCreateForm
        :course-id="courseId"
        @thread-created="onThreadCreated"
      />
    </template>

    <template #content>
      <!-- View Selector -->
      <div v-if="view === 'detail' && selectedThreadId" class="view-selector">
        <button @click="backToList" class="base-button small">
          ← Back to Discussions
        </button>
      </div>

      <!-- Thread List View -->
      <div v-if="view === 'list'">
        <ThreadList
          :course-id="courseId"
          :selected-thread-id="selectedThreadId"
          @thread-selected="onThreadSelected"
        />
      </div>

      <!-- Thread Detail View -->
      <div v-else-if="view === 'detail' && selectedThreadId">
        <ThreadDetail :thread-id="selectedThreadId" />
      </div>
    </template>
  </CollapsibleSection>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/modules/auth/store/auth.store'
import ThreadList from './ThreadList.vue'
import ThreadCreateForm from './ThreadCreateForm.vue'
import ThreadDetail from './ThreadDetail.vue'
import CollapsibleSection from '@/shared/components/CollapsibleSection.vue'

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
.login-hint {
  margin: 0;
  color: var(--color-text-muted);
  font-size: 0.875rem;
  font-weight: 500;
  background: var(--color-primary-light);
  padding: 0.625rem 1rem;
  border-radius: var(--radius-sm);
  border-left: 3px solid var(--color-primary);
}

.login-hint a {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 600;
  transition: all 0.2s;
}

.login-hint a:hover {
  color: var(--color-primary-hover);
  text-decoration: underline;
}

.view-selector {
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.view-selector .base-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}
</style>
