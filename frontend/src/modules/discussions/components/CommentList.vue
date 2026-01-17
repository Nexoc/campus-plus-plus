<template>
  <div class="comment-list">
    <h5>Comments</h5>

    <div v-if="loading" class="loading">Loading comments...</div>

    <div v-else-if="comments.length === 0" class="no-comments">
      No comments yet
    </div>

    <div v-else class="comments">
      <CommentItem
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
        @comment-updated="onCommentUpdated"
        @comment-deleted="onCommentDeleted"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useDiscussionsStore } from '../store/discussions.store'
import CommentItem from './CommentItem.vue'

interface Props {
  postId: string
}

const props = defineProps<Props>()

const discussionsStore = useDiscussionsStore()

const comments = computed(() => discussionsStore.comments)
const loading = computed(() => discussionsStore.loading)

function onCommentUpdated() {
  // Store handles update
}

function onCommentDeleted() {
  // Store handles delete
}

onMounted(async () => {
  await discussionsStore.loadComments(props.postId)
})
</script>

<style scoped>
.comment-list {
  margin-top: 0;
  padding-top: 0;
}

.comment-list h5 {
  margin: 0 0 1rem 0;
  font-size: 0.95rem;
  color: var(--color-text-primary);
}

.loading {
  color: var(--color-text-secondary);
  font-size: 0.9rem;
  padding: 0.5rem 0;
}

.no-comments {
  color: var(--color-text-secondary);
  font-size: 0.9rem;
  font-style: italic;
  padding: 0.5rem 0;
  opacity: 0.7;
}

.comments {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}
</style>
