<template>
  <div class="thread-list">
    <div v-if="loading" class="loading">Loading discussions...</div>
    
    <div v-else-if="!threads || threads.length === 0" class="empty-state">
      <p>No discussions yet. Be the first to start one!</p>
    </div>

    <div v-else class="threads">
      <div
        v-for="thread in threads"
        :key="thread.id"
        class="thread-item"
        @click="selectThread(thread.id)"
      >
        <h4>{{ thread.title }}</h4>
        <div class="thread-meta">
          <span class="post-count">{{ thread.postCount || 0 }} posts</span>
          <span class="created-at">{{ formatDate(thread.createdAt) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useDiscussionsStore } from '../store/discussions.store'
import { formatDate } from '@/shared/utils/dateFormatter'

interface Props {
  courseId: string
  selectedThreadId?: string
}

interface Emits {
  (e: 'thread-selected', threadId: string): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const discussionsStore = useDiscussionsStore()

const threads = computed(() => discussionsStore.threads)
const loading = computed(() => discussionsStore.loading)

function selectThread(threadId: string) {
  console.log('DEBUG ThreadList: selectThread called with ID:', threadId)
  console.log('DEBUG ThreadList: threadId type:', typeof threadId)
  emit('thread-selected', threadId)
}

onMounted(async () => {
  await discussionsStore.loadThreads(props.courseId)
})
</script>

<style scoped>
.thread-list {
  padding: 1rem;
}

.loading {
  text-align: center;
  color: var(--color-text-secondary);
  padding: 2rem;
}

.empty-state {
  text-align: center;
  color: var(--color-text-secondary);
  padding: 2rem;
  background: var(--color-surface);
  border-radius: 8px;
  margin: 1rem 0;
  border: 1px solid var(--color-border);
}

.threads {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.thread-item {
  padding: 1rem;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: var(--color-surface);
  cursor: pointer;
  transition: all 0.3s ease;
}

.thread-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-color: #007bff;
}

.thread-item h4 {
  margin: 0 0 0.5rem 0;
  color: var(--color-text-primary);
  font-size: 1rem;
}

.thread-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.875rem;
  color: var(--color-text-secondary);
}

.post-count {
  font-weight: 500;
}

.created-at {
  color: var(--color-text-secondary);
  opacity: 0.7;
}
</style>
