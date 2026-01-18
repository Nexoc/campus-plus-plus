<script setup lang="ts">
import http from '@/app/api/http';
import { useAuthStore } from '@/modules/auth/store/auth.store';
import {
  downloadMaterial,
  listByCourse,
  uploadMaterial,
  type CourseMaterial,
} from '@/modules/courses/api/courseMaterials.api';
import CollapsibleSection from '@/shared/components/CollapsibleSection.vue';
import { onMounted, ref } from 'vue';

/* ---------------- props ---------------- */

const props = defineProps<{
  courseId: string
}>()

/* ---------------- state ---------------- */

const auth = useAuthStore()

const materials = ref<CourseMaterial[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

// upload form state
const file = ref<File | null>(null)
const title = ref('')
const description = ref('')
const uploading = ref(false)

// edit state
const editingId = ref<string | null>(null)
const editTitle = ref('')
const editDescription = ref('')

/* ---------------- permissions ---------------- */

// IMPORTANT:
// role logic lives ONLY in auth.store
const canManage = (m: CourseMaterial) =>
  auth.isModerator || auth.user?.id === m.uploaderId

/* ---------------- api ---------------- */

async function loadMaterials() {
  loading.value = true
  error.value = null
  try {
    materials.value = await listByCourse(props.courseId)
  } catch {
    error.value = 'Failed to load materials'
  } finally {
    loading.value = false
  }
}

async function handleUpload() {
  if (!file.value) {
    error.value = 'Please select a file'
    return
  }

  uploading.value = true
  error.value = null

  try {
    await uploadMaterial(
      props.courseId,
      file.value,
      title.value || undefined,
      description.value || undefined
    )

    // reset form
    file.value = null
    title.value = ''
    description.value = ''

    await loadMaterials()
  } catch {
    error.value = 'Upload failed'
  } finally {
    uploading.value = false
  }
}

async function handleDownload(m: CourseMaterial) {
  try {
    const blob = await downloadMaterial(m.id)
    const url = URL.createObjectURL(blob)

    const a = document.createElement('a')
    a.href = url
    a.download = m.originalFilename
    document.body.appendChild(a)
    a.click()

    URL.revokeObjectURL(url)
    document.body.removeChild(a)
  } catch {
    error.value = 'Download failed'
  }
}

/* ---------------- edit / delete ---------------- */

function startEdit(m: CourseMaterial) {
  editingId.value = m.id
  editTitle.value = m.title ?? ''
  editDescription.value = m.description ?? ''
}

async function saveEdit(m: CourseMaterial) {
  try {
    await http.put(`/api/materials/${m.id}`, {
      title: editTitle.value || null,
      description: editDescription.value || null,
    })
    editingId.value = null
    await loadMaterials()
  } catch {
    error.value = 'Update failed'
  }
}

async function removeMaterial(m: CourseMaterial) {
  if (!confirm('Delete this material?')) return

  try {
    await http.delete(`/api/materials/${m.id}`)
    await loadMaterials()
  } catch {
    error.value = 'Delete failed'
  }
}

/* ---------------- lifecycle ---------------- */

onMounted(loadMaterials)
</script>

<template>
  <CollapsibleSection
    title="Course Materials"
    :show-toggle="auth.isAuthenticated"
    show-label="Upload Material"
    hide-label="Hide Form"
  >
    <template #content>
      <p v-if="loading">Loading materials…</p>
      <p v-if="error" class="error-message">{{ error }}</p>

      <!-- Materials List -->
      <ul v-if="materials.length" class="materials-list">
        <li v-for="m in materials" :key="m.id" class="material-item">
          <div class="material-main">
            <template v-if="editingId === m.id">
              <input v-model="editTitle" placeholder="Title" class="edit-input" />
              <textarea v-model="editDescription" placeholder="Description" class="edit-textarea" />
            </template>

            <template v-else>
              <strong>{{ m.title || m.originalFilename }}</strong>
              <span v-if="m.description"> — {{ m.description }}</span>
            </template>
          </div>

          <div class="item-actions">
            <button @click="handleDownload(m)" class="base-button small">Download</button>

            <template v-if="canManage(m)">
              <button
                v-if="editingId !== m.id"
                @click="startEdit(m)"
                class="base-button small"
              >
                Edit
              </button>

              <button
                v-else
                @click="saveEdit(m)"
                class="base-button small"
              >
                Save
              </button>

              <button
                class="base-button small danger"
                @click="removeMaterial(m)"
              >
                Delete
              </button>
            </template>
          </div>
        </li>
      </ul>

      <p v-else-if="!loading" class="empty-state">No materials yet.</p>
    </template>

    <template #form>
      <div class="form-content">
        <input
          type="file"
          @change="e => file = (e.target as HTMLInputElement).files?.[0] || null"
          accept=".pdf,image/png,image/jpeg"
          class="form-input"
        />

        <input
          type="text"
          v-model="title"
          placeholder="Title (optional)"
          class="form-input"
        />

        <textarea
          v-model="description"
          placeholder="Description (optional)"
          class="form-textarea"
        />

        <button
          :disabled="uploading"
          @click="handleUpload"
          class="base-button"
        >
          {{ uploading ? 'Uploading…' : 'Upload' }}
        </button>
      </div>
    </template>
  </CollapsibleSection>
</template>

<style scoped>
/* List items */
.materials-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.material-item {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: stretch;
  gap: 0.75rem;
  padding: 1.25rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface);
  transition: box-shadow 0.2s, border-color 0.2s;
}

.material-item:hover {
  border-color: var(--color-primary);
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.1);
}

@media (min-width: 640px) {
  .material-item {
    flex-direction: row;
    align-items: center;
  }
}

.material-main {
  flex: 1;
  min-width: 0;
}

.material-main strong {
  font-weight: 600;
  word-break: break-word;
  margin-right: 0.5rem;
  color: var(--color-text);
}

.material-main span {
  color: var(--color-text-muted);
  font-size: 0.9rem;
}

/* Edit inputs */
.edit-input,
.edit-textarea {
  width: 100%;
  margin-bottom: 0.5rem;
  padding: 0.625rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 0.95rem;
  background: var(--color-input-bg);
  color: var(--color-text);
  font-family: inherit;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.edit-input:focus,
.edit-textarea:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.edit-textarea {
  min-height: 60px;
  resize: vertical;
}

/* Actions */
.item-actions {
  display: flex;
  gap: 0.5rem;
  flex-shrink: 0;
  flex-wrap: wrap;
}

/* Form content */
.form-content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 0.625rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 0.95rem;
  font-family: inherit;
  background: var(--color-input-bg);
  color: var(--color-text);
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-textarea {
  min-height: 80px;
  resize: vertical;
}

.error-message {
  color: #7f1d1d;
  padding: 0.75rem 1rem;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: var(--radius-sm);
  margin-bottom: 1rem;
  font-weight: 500;
}

html[data-theme="dark"] .error-message {
  background: #7f1d1d;
  color: #fecaca;
  border-color: #dc2626;
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: var(--color-text-muted);
  font-style: italic;
  background: var(--color-primary-light);
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
}
</style>
