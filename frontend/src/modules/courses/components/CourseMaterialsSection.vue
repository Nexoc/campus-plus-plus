<script setup lang="ts">
import http from '@/app/api/http';
import { useAuthStore } from '@/modules/auth/store/auth.store';
import {
  downloadMaterial,
  listByCourse,
  uploadMaterial,
  type CourseMaterial,
} from '@/modules/courses/api/courseMaterials.api';
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
  <section class="course-materials">
    <h3>Course materials</h3>

    <p v-if="loading">Loading materials…</p>
    <p v-if="error" class="error">{{ error }}</p>

    <!-- List -->
    <ul v-if="materials.length">
      <li v-for="m in materials" :key="m.id" class="material-item">
        <div class="material-main">
          <template v-if="editingId === m.id">
            <input v-model="editTitle" placeholder="Title" />
            <textarea v-model="editDescription" placeholder="Description" />
          </template>

          <template v-else>
            <strong>{{ m.title || m.originalFilename }}</strong>
            <span v-if="m.description"> — {{ m.description }}</span>
          </template>
        </div>

        <div class="material-actions">
          <button @click="handleDownload(m)">Download</button>

          <template v-if="canManage(m)">
            <button
              v-if="editingId !== m.id"
              @click="startEdit(m)"
            >
              Edit
            </button>

            <button
              v-else
              @click="saveEdit(m)"
            >
              Save
            </button>

            <button
              class="danger"
              @click="removeMaterial(m)"
            >
              Delete
            </button>
          </template>
        </div>
      </li>
    </ul>

    <p v-else-if="!loading">No materials yet.</p>

    <!-- Upload -->
    <div v-if="auth.isAuthenticated" class="upload">
      <h4>Upload material</h4>

      <input
        type="file"
        @change="e => file = (e.target as HTMLInputElement).files?.[0] || null"
        accept=".pdf,image/png,image/jpeg"
      />

      <input
        type="text"
        v-model="title"
        placeholder="Title (optional)"
      />

      <textarea
        v-model="description"
        placeholder="Description (optional)"
      />

      <button
        :disabled="uploading"
        @click="handleUpload"
      >
        {{ uploading ? 'Uploading…' : 'Upload' }}
      </button>
    </div>
  </section>
</template>

<style scoped>
.course-materials {
  margin-top: 2rem;
  max-width: 900px;
}

/* ===== list ===== */

.material-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;

  padding: 12px 14px;
  margin-bottom: 10px;

  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fafafa;
}

.material-main {
  flex: 1;
  min-width: 0;
}

.material-main strong {
  display: block;
  font-weight: 600;
  word-break: break-word;
}

.material-main span {
  color: #555;
  font-size: 0.95rem;
}

/* edit inputs */
.material-main input,
.material-main textarea {
  width: 100%;
  margin-bottom: 6px;
  padding: 6px 8px;

  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 0.95rem;
}

/* ===== actions ===== */

.material-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.material-actions button {
  padding: 6px 10px;
  font-size: 0.85rem;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  background: #2563eb;
  color: #fff;
}

.material-actions button:hover {
  opacity: 0.9;
}

.material-actions button.danger {
  background: #c0392b;
}

/* ===== upload ===== */

.upload {
  margin-top: 2rem;
  padding: 16px;

  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;

  display: flex;
  flex-direction: column;
  gap: 10px;
}

.upload input[type="file"],
.upload input[type="text"],
.upload textarea {
  padding: 6px 8px;
  font-size: 0.95rem;

  border: 1px solid #d1d5db;
  border-radius: 4px;
}

.upload textarea {
  min-height: 70px;
  resize: vertical;
}

.upload button {
  align-self: flex-start;
  padding: 8px 14px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  background: #2563eb;
  color: #fff;
}

.upload button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* ===== misc ===== */

.error {
  color: #c0392b;
  margin-bottom: 8px;
}

</style>
