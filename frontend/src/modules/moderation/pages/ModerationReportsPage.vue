<template>
  <div class="detail-page">
    <div class="page-card">
      <div class="header">
        <h1>Review Moderation</h1>
        <p class="subtitle">Manage reported reviews and take moderation actions</p>
      </div>

      <!-- Status Filter -->
      <div class="filter-section">
        <div class="filter-group">
          <label for="status-filter">Filter by status:</label>
          <select id="status-filter" v-model="selectedStatus" @change="loadReports" class="status-select">
            <option value="PENDING">Pending</option>
            <option value="EDITED">Edited</option>
            <option value="RESOLVED">Resolved</option>
            <option value="REJECTED">Rejected</option>
          </select>
        </div>
      </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading">
      <p>Loading reports...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
      <button @click="loadReports" class="retry-button">Retry</button>
    </div>

    <!-- Empty State -->
    <div v-else-if="reports.length === 0" class="empty-state">
      <p>No {{ selectedStatus.toLowerCase() }} reports found.</p>
    </div>

    <!-- Reports List -->
    <div v-else class="reports-list">
      <div v-for="report in reports" :key="report.reportId" class="report-card">
        <!-- Report Header -->
        <div class="report-header">
          <span class="report-id">Report #{{ report.reportId.substring(0, 8) }}</span>
          <span :class="['report-status', report.status.toLowerCase()]">
            {{ report.status }}
          </span>
        </div>

        <!-- Report Details -->
        <div class="report-details">
          <div class="detail-row">
            <strong>Reported by:</strong>
            <span>{{ report.userName || 'Unknown User' }} <span class="user-id">(ID: {{ report.userId?.substring(0, 8) }})</span></span>
          </div>
          <div v-if="report.courseName" class="detail-row">
            <strong>Course:</strong>
            <router-link :to="`/courses/${report.courseId}`" class="course-link">{{ report.courseName }}</router-link>
          </div>
          <div class="detail-row">
            <strong>Reason:</strong>
            <span>{{ getReasonLabel(report.reason) }}</span>
          </div>
          <div class="detail-row">
            <strong>Reported:</strong>
            <span>{{ formatDate(report.createdAt) }}</span>
          </div>
        </div>

        <!-- Report Message -->
        <div class="report-message-section">
          <h3>Report Message</h3>
          <div class="report-message-content">
            <p v-if="report.comment">{{ report.comment }}</p>
            <p v-else class="no-message">User did not provide an optional message</p>
          </div>
        </div>

        <!-- Review Summary -->
        <div v-if="report.reviewId" class="review-summary">
          <h3 class="review-summary-header">Reported Review</h3>
          <div class="review-author">
            <strong>By:</strong> {{ report.reviewerNickname || 'Anonymous' }}
            <span v-if="report.moderationFlagged" class="edited-badge">✏️ Edited by Moderator</span>
          </div>
          <div class="review-content">
            <div class="review-rating">
              <span class="stars">{{ '★'.repeat(report.rating || 0) }}{{ '☆'.repeat(5 - (report.rating || 0)) }}</span>
              <span class="rating-text">{{ report.rating }}/5</span>
            </div>
            <p class="review-text">{{ report.reviewText || 'No review text available' }}</p>
            <p class="review-meta">Review ID: {{ report.reviewId.substring(0, 8) }}</p>
          </div>
        </div>

        <!-- Moderator Notes (for RESOLVED/REJECTED reports) -->
        <div v-if="report.status !== 'PENDING' && report.moderatorNotes" class="moderator-notes-display">
          <h3>Moderator Notes</h3>
          <div class="notes-content">
            <span class="notes-icon">📝</span>
            <p>{{ report.moderatorNotes }}</p>
          </div>
        </div>

        <!-- Moderation Actions (only for PENDING reports) -->
        <div v-if="report.status === 'PENDING'" class="moderation-actions">
          <h3>Moderation Actions</h3>
          <div class="action-buttons">
            <button 
              @click="showConfirmDialog(report, 'KEEP_VISIBLE')"
              class="action-button keep"
              :disabled="processing"
            >
              Keep Visible
            </button>
            <button 
              @click="showConfirmDialog(report, 'EDIT')"
              class="action-button edit"
              :disabled="processing"
            >
              Edit Review
            </button>
            <button 
              @click="showConfirmDialog(report, 'DELETE')"
              class="action-button delete"
              :disabled="processing"
            >
              Delete Review
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Confirmation Dialog -->
    <Teleport to="body">
      <div v-if="showConfirm" class="modal-overlay" @click="closeConfirmDialog">
        <div class="modal-dialog confirmation-modal" @click.stop>
          <h2>Confirm Action</h2>
          <p>
            Are you sure you want to <strong>{{ getActionText(confirmAction) }}</strong>?
          </p>
          <p v-if="confirmAction === 'DELETE'" class="warning-text">
            ⚠️ This action cannot be undone. The review will be permanently deleted.
          </p>
          
          <!-- Edit Review Text (only for EDIT action) -->
          <div v-if="confirmAction === 'EDIT'" class="form-group">
            <label for="edited-review-text">Edit Review Text <span class="required">*</span>:</label>
            <textarea 
              id="edited-review-text"
              v-model="editedReviewText"
              placeholder="Edit the review content to remove problematic parts..."
              rows="5"
              maxlength="2000"
              required
            ></textarea>
            <span class="char-count">{{ editedReviewText.length }}/2000</span>
          </div>

          <div class="form-group">
            <label for="moderator-notes">Moderator Notes (optional):</label>
            <textarea 
              id="moderator-notes"
              v-model="moderatorNotes"
              placeholder="Add notes about your decision..."
              rows="3"
              maxlength="500"
            ></textarea>
            <span class="char-count">{{ moderatorNotes.length }}/500</span>
          </div>

          <div class="modal-actions">
            <button @click="closeConfirmDialog" class="cancel-button" :disabled="processing">
              Cancel
            </button>
            <button 
              @click="confirmModeration" 
              :class="['confirm-button', confirmAction?.toLowerCase()]"
              :disabled="processing || (confirmAction === 'EDIT' && !editedReviewText.trim())"
            >
              {{ processing ? 'Processing...' : 'Confirm' }}
            </button>
          </div>

          <div v-if="confirmError" class="error-message">
            {{ confirmError }}
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Success Message -->
    <Teleport to="body">
      <div v-if="successMessage" class="success-toast">
        {{ successMessage }}
      </div>
    </Teleport>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { moderationApi } from '../api/moderationApi'
import type { ModerationReport, ModerationAction } from '../model/Moderation'
import { ReportReasonLabels } from '../model/Moderation'

const reports = ref<ModerationReport[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const selectedStatus = ref('PENDING')

const showConfirm = ref(false)
const confirmReport = ref<ModerationReport | null>(null)
const confirmAction = ref<ModerationAction | null>(null)
const moderatorNotes = ref('')
const editedReviewText = ref('')
const processing = ref(false)
const confirmError = ref<string | null>(null)
const successMessage = ref<string | null>(null)

onMounted(() => {
  loadReports()
})

async function loadReports() {
  loading.value = true
  error.value = null
  
  try {
    reports.value = await moderationApi.getReports(selectedStatus.value)
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Failed to load reports'
  } finally {
    loading.value = false
  }
}

function getReasonLabel(reason: string): string {
  return ReportReasonLabels[reason] || reason
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleString()
}

function showConfirmDialog(report: ModerationReport, action: ModerationAction) {
  confirmReport.value = report
  confirmAction.value = action
  moderatorNotes.value = ''
  editedReviewText.value = action === 'EDIT' ? (report.reviewText || '') : ''
  confirmError.value = null
  showConfirm.value = true
}

function closeConfirmDialog() {
  if (!processing.value) {
    showConfirm.value = false
    confirmReport.value = null
    confirmAction.value = null
    moderatorNotes.value = ''
    editedReviewText.value = ''
    confirmError.value = null
  }
}

function getActionText(action: ModerationAction | null): string {
  switch (action) {
    case 'KEEP_VISIBLE':
      return 'keep this review visible'
    case 'EDIT':
      return 'edit this review content'
    case 'DELETE':
      return 'permanently delete this review'
    default:
      return ''
  }
}

async function confirmModeration() {
  if (!confirmReport.value || !confirmAction.value) return
  
  // Validate edited review text for EDIT action
  if (confirmAction.value === 'EDIT' && !editedReviewText.value.trim()) {
    confirmError.value = 'Please provide the edited review text'
    return
  }
  
  processing.value = true
  confirmError.value = null
  
  try {
    await moderationApi.resolveReport(confirmReport.value.reportId, {
      action: confirmAction.value,
      moderatorNotes: moderatorNotes.value || undefined,
      editedReviewText: confirmAction.value === 'EDIT' ? editedReviewText.value : undefined
    })
    
    // Show success message
    successMessage.value = `Report resolved successfully with action: ${getActionText(confirmAction.value)}`
    setTimeout(() => {
      successMessage.value = null
    }, 5000)
    
    // Close dialog AFTER setting success message
    showConfirm.value = false
    confirmReport.value = null
    confirmAction.value = null
    moderatorNotes.value = ''
    editedReviewText.value = ''
    confirmError.value = null
    
    // Reload reports
    await loadReports()
  } catch (err: any) {
    if (err.response?.status === 403) {
      confirmError.value = 'Access denied: Moderator role required'
    } else if (err.response?.status === 404) {
      confirmError.value = 'Report or review not found'
    } else if (err.response?.status === 400) {
      confirmError.value = err.response?.data?.message || 'Invalid request'
    } else {
      confirmError.value = 'Failed to resolve report. Please try again.'
    }
  } finally {
    processing.value = false
  }
}
</script>

<style scoped>
.moderation-reports {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  background: transparent;
  color: var(--color-text);
}

.header {
  margin-bottom: 2rem;
}

.header h1 {
  margin: 0 0 0.5rem 0;
  font-size: 2rem;
  color: var(--color-text);
}

.subtitle {
  margin: 0;
  color: var(--color-text-muted);
  font-size: 1rem;
}

.filter-section {
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.filter-section label {
  font-weight: 500;
  color: var(--color-text);
}

.status-select {
  padding: 0.5rem 1rem;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  color: var(--color-text);
  font-size: 1rem;
  cursor: pointer;
}

.status-select:focus {
  outline: none;
  border-color: var(--color-primary);
}

.loading,
.empty-state {
  text-align: center;
  padding: 3rem;
  color: var(--color-text-muted);
  font-size: 1.1rem;
}

.error-message {
  background: #3a1a1a;
  border: 1px solid #8b0000;
  color: #ff6b6b;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.retry-button {
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background: #4a9eff;
  color: #ffffff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.retry-button:hover {
  background: #357abd;
}

.reports-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.report-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 1.5rem;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--color-border);
}

.report-id {
  font-family: monospace;
  color: var(--color-text-muted);
  font-size: 0.9rem;
}

.report-status {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
  text-transform: uppercase;
}

.report-status.pending {
  background: #4a4a1a;
  color: #ffeb3b;
}

.report-status.edited {
  background: #1a3a4a;
  color: #4a9eff;
}

.report-status.resolved {
  background: #1a4a1a;
  color: #4caf50;
}

.report-status.rejected {
  background: #3a1a1a;
  color: #ff6b6b;
}

.report-details {
  margin-bottom: 1.5rem;
}

.detail-row {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  color: var(--color-text);
}

.detail-row strong {
  min-width: 120px;
  color: var(--color-text-muted);
  flex-shrink: 0;
}

.user-id {
  font-family: monospace;
  color: var(--color-text-muted);
  font-size: 0.9em;
}

.course-link {
  color: #4a9eff;
  text-decoration: none;
  transition: color 0.2s;
}

.course-link:hover {
  color: #357abd;
  text-decoration: underline;
}

.comment-with-icon {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.info-icon {
  font-size: 1.3rem;
  cursor: help;
  flex-shrink: 0;
  color: #4a9eff;
  transition: transform 0.2s;
}

.info-icon:hover {
  transform: scale(1.2);
}

.report-message-section {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  padding: 1rem;
  margin-top: 1rem;
}

.report-message-section h3 {
  margin: 0 0 0.75rem 0;
  font-size: 1rem;
  color: var(--color-text);
}

.report-message-content p {
  margin: 0;
  color: var(--color-text);
  line-height: 1.6;
}

.report-message-content .no-message {
  color: #888;
  font-style: italic;
}

.review-summary {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  padding: 1rem;
  margin-top: 1rem;
}

.review-author {
  margin-bottom: 0.75rem;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.review-author strong {
  color: var(--color-text-muted);
}

.edited-badge {
  background: #4a4a1a;
  color: #ffeb3b;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.85rem;
  font-weight: 500;
}

.moderator-notes-display {
  background: #1a2a1a;
  border: 1px solid #2a4a2a;
  border-radius: 4px;
  padding: 1rem;
  margin-top: 1rem;
}

.moderator-notes-display h3 {
  margin: 0 0 0.75rem 0;
  font-size: 1rem;
  color: #4caf50;
}

.notes-content {
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;
  color: #ffffff;
}

.notes-icon {
  font-size: 1.2rem;
  flex-shrink: 0;
}

.notes-content p {
  margin: 0;
  line-height: 1.5;
}

.review-summary h3 {
  margin: 0 0 1rem 0;
  font-size: 1.1rem;
  color: #ffffff;
}

.review-content {
  color: #ffffff;
}

.review-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.stars {
  color: #ffeb3b;
  font-size: 1.2rem;
}

.rating-text {
  color: #aaaaaa;
  font-size: 0.9rem;
}

.review-text {
  margin: 0.5rem 0;
  color: #ffffff;
  line-height: 1.5;
}

.review-meta {
  margin: 0.5rem 0 0 0;
  color: #aaaaaa;
  font-size: 0.85rem;
  font-family: monospace;
}

.moderation-actions {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #333;
}

.moderation-actions h3 {
  margin: 0 0 1rem 0;
  font-size: 1rem;
  color: #ffffff;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.action-button {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.action-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.action-button.keep {
  background: #4caf50;
  color: #ffffff;
}

.action-button.keep:hover:not(:disabled) {
  background: #45a049;
}

.action-button.edit {
  background: #4a9eff;
  color: #ffffff;
}

.action-button.edit:hover:not(:disabled) {
  background: #357abd;
}

.action-button.delete {
  background: #f44336;
  color: #ffffff;
}

.action-button.delete:hover:not(:disabled) {
  background: #d32f2f;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-dialog {
  background: #1a1a1a;
  color: #ffffff;
  border: 1px solid #333;
  border-radius: 8px;
  padding: 2rem;
  max-width: 500px;
  width: 90%;
}

.confirmation-modal h2 {
  margin: 0 0 1rem 0;
  color: #ffffff;
}

.confirmation-modal p {
  margin: 0 0 1rem 0;
  color: #ffffff;
  line-height: 1.5;
}

.warning-text {
  color: #ff6b6b;
  font-weight: 500;
}

.form-group {
  margin: 1.5rem 0;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #ffffff;
  font-weight: 500;
}

.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  background: #2a2a2a;
  border: 1px solid #333;
  border-radius: 4px;
  color: #ffffff;
  font-family: inherit;
  font-size: 1rem;
  resize: vertical;
}

.form-group textarea:focus {
  outline: none;
  border-color: #4a9eff;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 0.85rem;
  color: #aaaaaa;
  margin-top: 0.25rem;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}

.cancel-button {
  padding: 0.75rem 1.5rem;
  background: #2a2a2a;
  color: #ffffff;
  border: 1px solid #333;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.cancel-button:hover:not(:disabled) {
  background: #333;
}

.confirm-button {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  color: #ffffff;
}

.confirm-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.confirm-button.keep_visible {
  background: #4caf50;
}

.confirm-button.keep_visible:hover:not(:disabled) {
  background: #45a049;
}

.confirm-button.edit {
  background: #4a9eff;
}

.confirm-button.edit:hover:not(:disabled) {
  background: #357abd;
}

.confirm-button.delete {
  background: #f44336;
}

.confirm-button.delete:hover:not(:disabled) {
  background: #d32f2f;
}

/* Success Toast */
.success-toast {
  position: fixed;
  top: 2rem;
  right: 2rem;
  background: #4caf50;
  color: #ffffff;
  padding: 1rem 1.5rem;
  border-radius: 4px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
  z-index: 2000;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

/* Light Mode Overrides */
:root:not([data-theme="dark"]) .moderation-reports {
  background: #f5f5f5;
  color: #333;
}

:root:not([data-theme="dark"]) .header h1 {
  color: #333;
}

:root:not([data-theme="dark"]) .subtitle {
  color: #666;
}

:root:not([data-theme="dark"]) .filter-section label {
  color: #333;
}

:root:not([data-theme="dark"]) .status-select {
  background: #ffffff;
  color: #333;
  border-color: #ddd;
}

:root:not([data-theme="dark"]) .report-card {
  background: #ffffff;
  border-color: #ddd;
}

:root:not([data-theme="dark"]) .report-id {
  color: #666;
}

:root:not([data-theme="dark"]) .report-message-section {
  background: #f9f9f9;
  border-color: #e0e0e0;
}

:root:not([data-theme="dark"]) .report-message-section h3 {
  color: #333;
}

:root:not([data-theme="dark"]) .report-message-content p {
  color: #333;
}

:root:not([data-theme="dark"]) .report-message-content .no-message {
  color: #999;
}

:root:not([data-theme="dark"]) .review-author {
  color: #333;
}

:root:not([data-theme="dark"]) .review-author strong {
  color: #555;
}

:root:not([data-theme="dark"]) .edited-badge {
  background: #fff9c4;
  color: #f57f17;
}

:root:not([data-theme="dark"]) .detail-row {
  color: #333;
}

:root:not([data-theme="dark"]) .detail-row strong {
  color: #555;
}

:root:not([data-theme="dark"]) .user-id {
  color: #999;
}

:root:not([data-theme="dark"]) .review-summary {
  background: #fafafa;
  border-color: #e0e0e0;
}

:root:not([data-theme="dark"]) .review-summary-header {
  color: #333 !important;
}

:root:not([data-theme="dark"]) .review-text {
  color: #333;
}

:root:not([data-theme="dark"]) .review-meta {
  color: #888;
}

:root:not([data-theme="dark"]) .moderator-notes-display {
  background: #e8f5e9;
  border-color: #4caf50;
}

:root:not([data-theme="dark"]) .moderator-notes-display h3 {
  color: #2e7d32;
}

:root:not([data-theme="dark"]) .moderator-notes-display p {
  color: #1b5e20;
}

:root:not([data-theme="dark"]) .modal-dialog {
  background: #ffffff;
  color: #333;
}

:root:not([data-theme="dark"]) .modal-dialog h2 {
  color: #333;
}

:root:not([data-theme="dark"]) .modal-dialog p {
  color: #333;
}

:root:not([data-theme="dark"]) .modal-dialog strong {
  color: #333;
}

:root:not([data-theme="dark"]) .confirmation-text {
  color: #333;
}

:root:not([data-theme="dark"]) .warning-text {
  color: #d84315 !important;
  background: #fff3e0;
}

:root:not([data-theme="dark"]) .modal-dialog textarea,
:root:not([data-theme="dark"]) .modal-dialog input {
  background: #fafafa;
  color: #333;
  border-color: #ddd;
}

:root:not([data-theme="dark"]) .modal-dialog label {
  color: #555;
}

:root:not([data-theme="dark"]) .cancel-button {
  background: #f5f5f5;
  color: #333;
  border-color: #ddd;
}

:root:not([data-theme="dark"]) .cancel-button:hover:not(:disabled) {
  background: #e0e0e0;
}

:root:not([data-theme="dark"]) .error-message {
  background: #ffebee;
  border-color: #ef5350;
  color: #c62828;
}
</style>
