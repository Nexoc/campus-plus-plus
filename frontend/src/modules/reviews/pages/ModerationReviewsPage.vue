<template>
  <div class="moderation-page">
    <div class="page-card">
      <h1>Moderation - Reviews</h1>

      <div class="filters">
        <input
          v-model="filters.courseId"
          type="text"
          placeholder="Filter by course ID..."
          class="search-input"
        />

        <label class="checkbox-filter">
          <input v-model="filters.flaggedOnly" type="checkbox" />
          Show flagged only
        </label>

        <button @click="loadReviews" class="base-button">
          Refresh
        </button>
      </div>

      <div v-if="loading" class="loading">Loading reviews...</div>

      <table v-else class="moderation-table">
        <thead>
          <tr>
            <th>User</th>
            <th>Course</th>
            <th>Title</th>
            <th>Rating</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="review in reviews" :key="review.reviewId" :class="{ flagged: review.isModerationFlagged }">
            <td>{{ review.userName || 'Anonymous' }}</td>
            <td>{{ review.courseId }}</td>
            <td>{{ review.title }}</td>
            <td>{{ review.rating }}/5</td>
            <td>
              <span v-if="review.isModerationFlagged" class="badge-warning">
                Flagged
              </span>
              <span v-else class="badge-success">
                Active
              </span>
            </td>
            <td class="actions">
              <button
                v-if="!review.isModerationFlagged"
                @click="showFlagModal(review)"
                class="small-button warning"
              >
                Flag
              </button>
              <button
                v-else
                @click="unflagReview(review.reviewId!)"
                class="small-button"
              >
                Unflag
              </button>
              <button
                @click="showDeleteModal(review)"
                class="small-button danger"
              >
                Remove
              </button>
              <button
                @click="viewDetails(review)"
                class="small-button"
              >
                View
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="reviews.length === 0" class="empty-state">
        No reviews to moderate
      </div>
    </div>

    <!-- Detail Modal -->
    <div v-if="selectedReview" class="modal-overlay" @click.self="selectedReview = null">
      <div class="modal">
        <div class="modal-header">
          <h2>Review Details</h2>
          <button class="close-button" @click="selectedReview = null">×</button>
        </div>
        <div class="modal-body">
          <p><strong>User:</strong> {{ selectedReview.userName || 'Anonymous' }}</p>
          <p><strong>Course:</strong> {{ selectedReview.courseId }}</p>
          <p><strong>Rating:</strong> {{ selectedReview.rating }}/5</p>
          <p><strong>Title:</strong> {{ selectedReview.title }}</p>
          <p><strong>Content:</strong></p>
          <p class="content-preview">{{ selectedReview.content }}</p>
          <p v-if="selectedReview.isModerationFlagged">
            <strong>Flagged:</strong> {{ selectedReview.moderationReason }}
          </p>
        </div>
        <div class="modal-footer">
          <button @click="selectedReview = null" class="base-button secondary">
            Close
          </button>
        </div>
      </div>
    </div>

    <!-- Flag Modal -->
    <div v-if="flagModal.show" class="modal-overlay" @click.self="flagModal.show = false">
      <div class="modal">
        <div class="modal-header">
          <h2>Flag Review</h2>
          <button class="close-button" @click="flagModal.show = false">×</button>
        </div>
        <div class="modal-body">
          <p>Review: {{ flagModal.review?.title }}</p>
          <textarea
            v-model="flagModal.reason"
            placeholder="Reason for flagging..."
            rows="5"
          ></textarea>
        </div>
        <div class="modal-footer">
          <button @click="submitFlag" class="base-button warning">
            Flag
          </button>
          <button @click="flagModal.show = false" class="base-button secondary">
            Cancel
          </button>
        </div>
      </div>
    </div>

    <!-- Delete Modal -->
    <div v-if="deleteModal.show" class="modal-overlay" @click.self="deleteModal.show = false">
      <div class="modal">
        <div class="modal-header">
          <h2>Remove Review</h2>
          <button class="close-button" @click="deleteModal.show = false">×</button>
        </div>
        <div class="modal-body">
          <p>Are you sure you want to remove this review?</p>
          <p><strong>Title:</strong> {{ deleteModal.review?.title }}</p>
          <textarea
            v-model="deleteModal.reason"
            placeholder="Reason for removal..."
            rows="3"
          ></textarea>
        </div>
        <div class="modal-footer">
          <button @click="submitDelete" class="base-button danger">
            Remove
          </button>
          <button @click="deleteModal.show = false" class="base-button secondary">
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { reviewsApi } from '../api/reviewsApi'
import type { Review } from '../model/Review'

const reviews = ref<Review[]>([])
const loading = ref(false)
const selectedReview = ref<Review | null>(null)

const filters = ref({
  courseId: '',
  flaggedOnly: false,
})

const flagModal = ref({
  show: false,
  review: null as Review | null,
  reason: '',
})

const deleteModal = ref({
  show: false,
  review: null as Review | null,
  reason: '',
})

const loadReviews = async () => {
  loading.value = true
  try {
    const response = await reviewsApi.moderationGetAll({
      courseId: filters.value.courseId || undefined,
      flagged: filters.value.flaggedOnly || undefined,
    })
    reviews.value = response.data
  } catch (error) {
    console.error('Failed to load reviews', error)
  } finally {
    loading.value = false
  }
}

const viewDetails = (review: Review) => {
  selectedReview.value = review
}

const showFlagModal = (review: Review) => {
  flagModal.value.review = review
  flagModal.value.reason = ''
  flagModal.value.show = true
}

const submitFlag = async () => {
  if (flagModal.value.review && flagModal.value.reason) {
    try {
      await reviewsApi.moderationFlag(
        flagModal.value.review.reviewId!,
        flagModal.value.reason
      )
      flagModal.value.show = false
      await loadReviews()
    } catch (error) {
      console.error('Failed to flag review', error)
    }
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

const showDeleteModal = (review: Review) => {
  deleteModal.value.review = review
  deleteModal.value.reason = ''
  deleteModal.value.show = true
}

const submitDelete = async () => {
  if (deleteModal.value.review && deleteModal.value.reason) {
    try {
      await reviewsApi.moderationDelete(
        deleteModal.value.review.reviewId!,
        deleteModal.value.reason
      )
      deleteModal.value.show = false
      await loadReviews()
    } catch (error) {
      console.error('Failed to remove review', error)
    }
  }
}

// Load reviews on mount
loadReviews()
</script>

<style scoped>
.moderation-page {
  padding: 2rem;
}

.page-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.page-card h1 {
  margin-bottom: 1.5rem;
}

.filters {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.checkbox-filter {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  white-space: nowrap;
}

.moderation-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1rem;
}

.moderation-table thead {
  background: #f5f5f5;
}

.moderation-table th,
.moderation-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.moderation-table th {
  font-weight: 600;
}

.moderation-table tbody tr:hover {
  background: #f9f9f9;
}

.moderation-table tbody tr.flagged {
  background: #fff3cd;
}

.badge-warning {
  background: #ffc107;
  color: black;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 0.85rem;
  font-weight: 500;
}

.badge-success {
  background: #28a745;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 0.85rem;
  font-weight: 500;
}

.actions {
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
  white-space: nowrap;
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

.empty-state {
  text-align: center;
  padding: 3rem 2rem;
  color: #666;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #666;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 8px;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #666;
}

.modal-body {
  padding: 1.5rem;
}

.modal-body p {
  margin-bottom: 1rem;
}

.content-preview {
  background: #f5f5f5;
  padding: 1rem;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
}

.modal-body textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: inherit;
  resize: vertical;
}

.modal-footer {
  padding: 1.5rem;
  border-top: 1px solid #eee;
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}
</style>
