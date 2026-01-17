<template>
  <div class="reviews-section">
    <h2>Reviews</h2>

    <!-- Average Rating Display -->
    <div v-if="summary" class="rating-summary">
      <div class="rating-display">
        <span class="average-rating">
          <template v-if="summary.averageRating !== null">
            ★ {{ summary.averageRating.toFixed(1) }} / 5
          </template>
          <template v-else>
            ★ No ratings yet
          </template>
        </span>
        <span class="review-count">
          ({{ summary.reviewCount }} {{ summary.reviewCount === 1 ? 'review' : 'reviews' }})
        </span>
      </div>
    </div>

    <!-- Success Message -->
    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>

    <!-- Write Review Button (Students & Moderators) -->
    <div v-if="auth.isAuthenticated && auth.user?.role !== 'APPLICANT' && !userHasReviewed" class="write-review-section">
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

          <!-- Report button for other users' reviews (authenticated users only) -->
          <div v-else-if="canReportReview(review)" class="review-actions">
            <button 
              class="small-button report-button"
              @click="openReportModal(review.reviewId!)"
              title="Report inappropriate review"
            >
              Report
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

    <!-- Report Modal -->
    <ReportModal
      :is-open="showReportModal"
      target-type="REVIEW"
      :target-id="reportingReviewId"
      @close="closeReportModal"
      @success="handleReportSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { computed, ref } from 'vue'
import { reviewsApi } from '../api/reviewsApi'
import type { Review, ReviewSummary, CreateReviewRequest } from '../model/Review'
import ReportModal from '@/modules/reports/components/ReportModal.vue'

interface Props {
  courseId: string
}

const props = defineProps<Props>()
const auth = useAuthStore()

const reviews = ref<Review[]>([])
const summary = ref<ReviewSummary | null>(null)
const loading = ref(false)
const showCreateForm = ref(false)
const editingId = ref<string | null>(null)
const error = ref<string>('')
const successMessage = ref<string>('')

// Report modal state
const showReportModal = ref(false)
const reportingReviewId = ref('')
const reportedReviewIds = ref<Set<string>>(new Set())

const formData = ref<CreateReviewRequest>({
  courseId: props.courseId,
  rating: 0,
  text: '',
})

// Computed
const currentUserId = computed(() => auth.user?.id || '')

const userHasReviewed = computed(() => {
  return reviews.value.some(review => review.userId === currentUserId.value)
})

// Methods
const isOwnReview = (review: Review) => {
  return auth.isAuthenticated && review.userId === currentUserId.value
}

const canReportReview = (review: Review) => {
  // Only authenticated users who are not the review author can report
  // Applicants (unauthenticated) cannot report
  // Cannot report if already reported
  return auth.isAuthenticated && 
         auth.user?.role !== 'APPLICANT' && 
         !isOwnReview(review) &&
         !reportedReviewIds.value.has(review.reviewId || '')
}

const openReportModal = (reviewId: string) => {
  reportingReviewId.value = reviewId
  showReportModal.value = true
}

const closeReportModal = () => {
  showReportModal.value = false
  reportingReviewId.value = ''
}

const handleReportSuccess = () => {
  // Mark the review as reported
  if (reportingReviewId.value) {
    reportedReviewIds.value.add(reportingReviewId.value)
  }
  
  // Close the modal
  closeReportModal()
  
  // Show success message
  successMessage.value = 'Report submitted successfully. Thank you for helping maintain our community standards.'
  setTimeout(() => {
    successMessage.value = ''
  }, 5000)
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

const loadSummary = async () => {
  try {
    const response = await reviewsApi.getSummary(props.courseId)
    summary.value = response.data
  } catch (err: any) {
    console.error('Failed to load review summary', err)
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
    await loadSummary()
    
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
      await loadSummary()
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

// Load reviews and summary on mount
loadReviews()
loadSummary()
</script>

<style scoped>
.reviews-section {
  margin-top: 3rem;
  padding: 2rem;
  background: var(--color-background-secondary);
  border-radius: 8px;
}

.reviews-section h2 {
  margin-bottom: 1rem;
  color: var(--color-text-primary);
}

.rating-summary {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: var(--color-surface);
  border-radius: 8px;
  border-left: 4px solid #ffc107;
}

.rating-display {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.average-rating {
  font-size: 1.5rem;
  font-weight: bold;
  color: #ffc107;
}

.review-count {
  font-size: 1rem;
  color: var(--color-text-secondary);
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
  background: var(--color-surface);
  padding: 1.5rem;
  border-radius: 8px;
  margin-top: 1rem;
  border: 1px solid var(--color-border);
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: var(--color-text-primary);
}

.form-group .required {
  color: #dc3545;
  font-weight: bold;
}

.form-group .optional {
  color: var(--color-text-secondary);
  font-weight: normal;
  font-size: 0.9rem;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-family: inherit;
  background: var(--color-background);
  color: var(--color-text-primary);
}

.form-group select {
  cursor: pointer;
}

.form-group select option {
  background: var(--color-background);
  color: var(--color-text-primary);
  padding: 0.5rem;
}

:root[data-theme='dark'] .form-group select option {
  background: #1f2937;
  color: #f9fafb;
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
  background: var(--color-surface);
  padding: 1.5rem;
  border-radius: 8px;
  border-left: 4px solid #007bff;
  border: 1px solid var(--color-border);
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
  color: var(--color-text-secondary);
}

.review-meta strong {
  color: var(--color-text-primary);
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

.small-button.report-button {
  background: #6c757d;
  color: white;
}

.small-button.report-button:hover {
  background: #5a6268;
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
  color: var(--color-text-primary);
  line-height: 1.6;
  margin: 0.5rem 0 1rem;
}

.review-content.no-text {
  font-style: italic;
  color: var(--color-text-secondary);
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: var(--color-text-secondary);
}
</style>
