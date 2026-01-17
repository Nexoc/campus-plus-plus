<template>
  <Teleport to="body">
    <div v-if="isOpen" class="modal-overlay" @click="closeModal">
      <div class="modal-dialog" @click.stop>
        <div class="modal-header">
          <h3>Report Review</h3>
          <button class="close-button" @click="closeModal" aria-label="Close">×</button>
        </div>

        <div class="modal-body">
          <div v-if="error" class="error-message">
            {{ error }}
          </div>

          <form @submit.prevent="submitReport">
            <div class="form-group">
              <label for="reason">Reason <span class="required">*</span></label>
              <select 
                id="reason" 
                v-model="formData.reason" 
                required
                class="form-control"
              >
                <option value="">Select a reason...</option>
                <option v-for="(label, reason) in reasonLabels" :key="reason" :value="reason">
                  {{ label }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label for="comment">
                Additional details
                <span class="optional">(optional)</span>
              </label>
              <textarea
                id="comment"
                v-model="formData.comment"
                placeholder="Provide additional context for your report..."
                rows="4"
                class="form-control"
                maxlength="500"
              ></textarea>
              <small class="form-text">{{ formData.comment?.length || 0 }} / 500</small>
            </div>

            <div class="modal-footer">
              <button 
                type="button" 
                class="base-button secondary" 
                @click="closeModal"
                :disabled="loading"
              >
                Cancel
              </button>
              <button 
                type="submit" 
                class="base-button danger" 
                :disabled="loading || !formData.reason"
              >
                {{ loading ? 'Submitting...' : 'Submit Report' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { reportsApi } from '../api/reportsApi'
import { ReportReasonLabels, type CreateReportRequest, type ReportReason } from '../model/Report'

interface Props {
  isOpen: boolean
  targetType: string
  targetId: string
}

interface Emits {
  (e: 'close'): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const loading = ref(false)
const error = ref('')
const reasonLabels = ReportReasonLabels

const formData = ref<CreateReportRequest>({
  targetType: props.targetType,
  targetId: props.targetId,
  reason: '' as ReportReason,
  comment: ''
})

// Watch for prop changes to update form data
watch(() => [props.targetType, props.targetId], () => {
  formData.value.targetType = props.targetType
  formData.value.targetId = props.targetId
})

// Reset form when modal closes
watch(() => props.isOpen, (isOpen) => {
  if (!isOpen) {
    resetForm()
  }
})

const resetForm = () => {
  formData.value = {
    targetType: props.targetType,
    targetId: props.targetId,
    reason: '' as ReportReason,
    comment: ''
  }
  error.value = ''
}

const closeModal = () => {
  if (!loading.value) {
    emit('close')
  }
}

const submitReport = async () => {
  if (!formData.value.reason) {
    error.value = 'Please select a reason'
    return
  }

  loading.value = true
  error.value = ''

  try {
    await reportsApi.create(formData.value)
    // Success - notify parent first, then close modal
    emit('success')
    // Small delay to ensure parent processes the success
    await new Promise(resolve => setTimeout(resolve, 100))
    closeModal()
  } catch (err: any) {
    console.error('Failed to submit report:', err)
    
    if (err.response?.status === 409) {
      error.value = 'You have already reported this review'
    } else if (err.response?.status === 400) {
      error.value = err.response?.data?.message || 'Invalid report data'
    } else if (err.response?.status === 401) {
      error.value = 'You must be logged in to report reviews'
    } else if (err.response?.status === 404) {
      error.value = 'Review not found'
    } else {
      error.value = 'Failed to submit report. Please try again.'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-dialog {
  background: #1a1a1a;
  color: #ffffff;
  border-radius: 8px;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
  border: 1px solid #333;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #333;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  transition: color 0.2s;
}

.close-button:hover {
  color: #ffffff;
}

.modal-body {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #444;
  border-radius: 4px;
  font-size: 1rem;
  background: #2a2a2a;
  color: #ffffff;
}

.form-control option {
  background: #2a2a2a;
  color: #ffffff;
}

.form-control:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.form-text {
  display: block;
  margin-top: 0.25rem;
  color: #999;
  font-size: 0.875rem;
}

.required {
  color: #dc3545;
}

.optional {
  color: #999;
  font-weight: normal;
}

.error-message {
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
  padding: 0.75rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding-top: 1rem;
  border-top: 1px solid #333;
}

.base-button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.base-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.base-button.secondary {
  background-color: #6c757d;
  color: white;
}

.base-button.secondary:hover:not(:disabled) {
  background-color: #5a6268;
}

.base-button.danger {
  background-color: #dc3545;
  color: white;
}

.base-button.danger:hover:not(:disabled) {
  background-color: #c82333;
}
</style>
