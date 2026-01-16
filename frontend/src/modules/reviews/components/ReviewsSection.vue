<template>
  <div class="reviews-section">
    <h2>Reviews ({{ reviews.length }})</h2>

    <!-- Write Review Button (Students & Moderators) -->
    <div v-if="auth.isAuthenticated && auth.user?.role !== 'APPLICANT'" class="write-review-section">
      <button 
        v-if="!showCreateForm"
        class="base-button"
        @click="showCreateForm = true"
      >
        Write a Review
      </button>

      <!-- Create Review Form -->
      <form v-if="showCreateForm" @submit.prevent="submitReview" class="review-form">
        <div class="form-group">
          <label>Rating</label>
          <select v-model.number="formData.rating" required>
            <option value="">Select rating...</option>
            <option value="5">5 - Excellent</option>
            <option value="4">4 - Good</option>
            <option value="3">3 - Average</option>
            <option value="2">2 - Poor</option>
            <option value="1">1 - Very Poor</option>
          </select>
        </div>

        <div class="form-group">
          <label>Title</label>
          <input 
            v-model="formData.title"
            type="text"
            placeholder="Review title..."
            required
            maxlength="100"
          />
        </div>

        <div class="form-group">
          <label>Content</label>
          <textarea
            v-model="formData.content"
            placeholder="Share your thoughts..."
            required
            rows="5"
          ></textarea>
        </div>

        <div class="form-actions">
          <button type="submit" class="base-button" :disabled="loading">
            {{ loading ? 'Submitting...' : 'Post Review' }}
          </button>
          <button 
            type="button"
            class="base-button secondary"
            @click="cancelCreate"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>

    <!-- Reviews List -->
    <div v-if="reviews.length > 0" class="reviews-list">
      <article v-for="review in reviews" :key="review.reviewId" class="review-card">
        <div class="review-header">
          <div class="review-meta">
            <strong>{{ review.userName || 'Anonymous' }}</strong>
            <span class="review-date">{{ formatDate(review.createdAt) }}</span>
            <span class="review-rating">★ {{ review.rating }}/5</span>
          </div>

          <!-- Edit/Delete for own reviews -->
          <div v-if="isOwnReview(review)" class="review-actions">
            <button 
              class="small-button"
              @click="startEdit(review)"
            >
              Edit
            </button>
            <button 
              class="small-button danger"
              @click="deleteReview(review.reviewId!)"
            >
              Delete
            </button>
          </div>

          <!-- Moderation actions -->
          <div v-if="auth.isModerator" class="moderation-actions">
            <button
              v-if="!review.isModerationFlagged"
              class="small-button warning"
              @click="flagReview(review.reviewId!, 'Inappropriate content')"
            >
              Flag
            </button>
            <button
              v-else
              class="small-button"
              @click="unflagReview(review.reviewId!)"
            >
              Unflag
            </button>
            <button
              class="small-button danger"
              @click="deleteReviewAsModerator(review.reviewId!)"
            >
              Remove
            </button>
          </div>
        </div>

        <h3 class="review-title">{{ review.title }}</h3>
        <p class="review-content">{{ review.content }}</p>

        <!-- Moderation badge -->
        <div v-if="review.isModerationFlagged" class="moderation-badge">
          Flagged: {{ review.moderationReason }}
        </div>

        <div class="review-footer">
          <button class="helpful-button" @click="markHelpful(review.reviewId!)">
            👍 Helpful ({{ review.helpful }})
          </button>
          <button class="helpful-button" @click="markNotHelpful(review.reviewId!)">
            👎 Not Helpful ({{ review.notHelpful }})
          </button>
        </div>
      </article>
    </div>

    <!-- No reviews message -->
    <div v-else class="empty-state">
      <p>No reviews yet. Be the first to share your thoughts!</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { computed, ref } from 'vue'
import { reviewsApi } from '../api/reviewsApi'
import type { Review, CreateReviewRequest } from '../model/Review'

interface Props {
  courseId: string
}

const props = defineProps<Props>()
const auth = useAuthStore()

const reviews = ref<Review[]>([])
const loading = ref(false)
const showCreateForm = ref(false)
const editingId = ref<string | null>(null)

const formData = ref<CreateReviewRequest>({
  courseId: props.courseId,
  rating: 0,
  title: '',
  content: '',
})

// Computed
const currentUserId = computed(() => auth.user?.id || '')

// Methods
const isOwnReview = (review: Review) => {
  return auth.isAuthenticated && review.userId === currentUserId.value
}

const formatDate = (date?: string) => {
  if (!date) return 'Unknown date'
  return new Date(date).toLocaleDateString()
}

const loadReviews = async () => {
  try {
    const response = await reviewsApi.getByCourse(props.courseId)
    reviews.value = response.data
  } catch (error) {
    console.error('Failed to load reviews', error)
  }
}

const submitReview = async () => {
  loading.value = true
  try {
    if (editingId.value) {
      await reviewsApi.update(editingId.value, {
        rating: formData.value.rating,
        title: formData.value.title,
        content: formData.value.content,
      })
      editingId.value = null
    } else {
      await reviewsApi.create(props.courseId, formData.value)
    }
    resetForm()
    await loadReviews()
  } catch (error) {
    console.error('Failed to submit review', error)
  } finally {
    loading.value = false
  }
}

const cancelCreate = () => {
  resetForm()
  editingId.value = null
  showCreateForm.value = false
}

const resetForm = () => {
  formData.value = {
    courseId: props.courseId,
    rating: 0,
    title: '',
    content: '',
  }
}

const startEdit = (review: Review) => {
  editingId.value = review.reviewId || null
  formData.value = {
    courseId: props.courseId,
    rating: review.rating,
    title: review.title,
    content: review.content,
  }
  showCreateForm.value = true
}

const deleteReview = async (reviewId: string) => {
  if (confirm('Delete this review?')) {
    try {
      await reviewsApi.delete(reviewId)
      await loadReviews()
    } catch (error) {
      console.error('Failed to delete review', error)
    }
  }
}

const deleteReviewAsModerator = async (reviewId: string) => {
  const reason = prompt('Reason for removal:')
  if (reason) {
    try {
      await reviewsApi.moderationDelete(reviewId, reason)
      await loadReviews()
    } catch (error) {
      console.error('Failed to remove review', error)
    }
  }
}

const flagReview = async (reviewId: string, reason: string) => {
  try {
    await reviewsApi.moderationFlag(reviewId, reason)
    await loadReviews()
  } catch (error) {
    console.error('Failed to flag review', error)
  }
}

const unflagReview = async (reviewId: string) => {
  try {
    await reviewsApi.moderationUnflag(reviewId)
    await loadReviews()
  } catch (error) {
    console.error('Failed to unflag review', error)
  }
}

const markHelpful = async (reviewId: string) => {
  // TODO: Implement helpful marking endpoint
  console.log('Mark helpful:', reviewId)
}

const markNotHelpful = async (reviewId: string) => {
  // TODO: Implement not helpful marking endpoint
  console.log('Mark not helpful:', reviewId)
}

// Load reviews on mount
loadReviews()
</script>

<style scoped>
.reviews-section {
  margin-top: 3rem;
  padding: 2rem;
  background: var(--color-background-secondary);
  border-radius: 8px;
}

.reviews-section h2 {
  margin-bottom: 1.5rem;
  color: var(--color-text-primary);
}

.write-review-section {
  margin-bottom: 2rem;
}

.review-form {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  margin-top: 1rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: inherit;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.review-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  border-left: 4px solid #007bff;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.review-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.9rem;
  color: #666;
}

.review-date {
  opacity: 0.7;
}

.review-rating {
  font-weight: 500;
  color: #ffc107;
}

.review-actions,
.moderation-actions {
  display: flex;
  gap: 0.5rem;
}

.small-button {
  padding: 0.4rem 0.8rem;
  font-size: 0.85rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.small-button:hover {
  background: #0056b3;
}

.small-button.warning {
  background: #ffc107;
  color: black;
}

.small-button.warning:hover {
  background: #ffb300;
}

.small-button.danger {
  background: #dc3545;
}

.small-button.danger:hover {
  background: #c82333;
}

.review-title {
  margin: 0.5rem 0;
  color: var(--color-text-primary);
}

.review-content {
  color: #555;
  line-height: 1.6;
  margin: 0.5rem 0 1rem;
}

.moderation-badge {
  background: #fff3cd;
  color: #856404;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.review-footer {
  display: flex;
  gap: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.helpful-button {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  font-size: 0.9rem;
}

.helpful-button:hover {
  color: #007bff;
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: #666;
}
</style>
