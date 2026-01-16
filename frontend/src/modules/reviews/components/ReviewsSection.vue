<template>
  <div class="reviews-section">
    <h2>Reviews ({{ reviews.length }})</h2>

    <!-- Success Message -->
    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>

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
        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <div class="form-group">
          <label>Rating <span class="required">*</span></label>
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
          <label>Review Text <span class="optional">(optional)</span></label>
          <textarea
            v-model="formData.text"
            placeholder="Share your thoughts about this course..."
            rows="5"
          ></textarea>
        </div>

        <div class="form-actions">
          <button type="submit" class="base-button" :disabled="loading || !formData.rating">
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
        </div>

        <p v-if="review.text" class="review-content">{{ review.text }}</p>
        <p v-else class="review-content no-text">No additional comments</p>
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
const error = ref<string>('')
const successMessage = ref<string>('')

const formData = ref<CreateReviewRequest>({
  courseId: props.courseId,
  rating: 0,
  text: '',
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
  } catch (err: any) {
    console.error('Failed to load reviews', err)
    error.value = err.response?.data?.message || 'Failed to load reviews'
  }
}

const submitReview = async () => {
  // Validate rating
  if (!formData.value.rating || formData.value.rating < 1 || formData.value.rating > 5) {
    error.value = 'Please select a rating between 1 and 5'
    return
  }

  error.value = ''
  loading.value = true
  
  try {
    if (editingId.value) {
      await reviewsApi.update(editingId.value, {
        rating: formData.value.rating,
        text: formData.value.text,
      })
      successMessage.value = 'Review updated successfully!'
      editingId.value = null
    } else {
      await reviewsApi.create(props.courseId, formData.value)
      successMessage.value = 'Review posted successfully!'
    }
    
    resetForm()
    showCreateForm.value = false
    await loadReviews()
    
    // Clear success message after 3 seconds
    setTimeout(() => {
      successMessage.value = ''
    }, 3000)
  } catch (err: any) {
    console.error('Failed to submit review', err)
    error.value = err.response?.data?.message || 'Failed to submit review'
  } finally {
    loading.value = false
  }
}

const cancelCreate = () => {
  resetForm()
  editingId.value = null
  showCreateForm.value = false
  error.value = ''
}

const resetForm = () => {
  formData.value = {
    courseId: props.courseId,
    rating: 0,
    text: '',
  }
}

const startEdit = (review: Review) => {
  editingId.value = review.reviewId || null
  formData.value = {
    courseId: props.courseId,
    rating: review.rating,
    text: review.text || '',
  }
  showCreateForm.value = true
  error.value = ''
}

const deleteReview = async (reviewId: string) => {
  if (confirm('Are you sure you want to delete this review?')) {
    try {
      await reviewsApi.delete(reviewId)
      await loadReviews()
      successMessage.value = 'Review deleted successfully'
      setTimeout(() => {
        successMessage.value = ''
      }, 3000)
    } catch (err: any) {
      console.error('Failed to delete review', err)
      error.value = err.response?.data?.message || 'Failed to delete review'
    }
  }
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

.success-message {
  background: #d4edda;
  color: #155724;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
  border: 1px solid #c3e6cb;
}

.error-message {
  background: #f8d7da;
  color: #721c24;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
  border: 1px solid #f5c6cb;
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

.form-group .required {
  color: #dc3545;
  font-weight: bold;
}

.form-group .optional {
  color: #666;
  font-weight: normal;
  font-size: 0.9rem;
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

.review-content {
  color: #555;
  line-height: 1.6;
  margin: 0.5rem 0 1rem;
}

.review-content.no-text {
  font-style: italic;
  color: #999;
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: #666;
}
</style>
