<script setup lang="ts">
import { ensureCsrf } from '@/app/security/csrf'
import type { UserRole } from '@/modules/admin/api/admin-user.api'
import { useAdminUserStore } from '@/modules/admin/store/admin-user.store'
import { useAuthStore } from '@/modules/auth/store/auth.store'
import EntityTable from '@/shared/components/EntityTable.vue'
import { computed, onMounted, ref } from 'vue'


const adminUserStore = useAdminUserStore()
const auth = useAuthStore()
const currentUserId = computed(() => auth.user?.id)

// Table configuration
const tableColumns = [
  { key: 'email', label: 'Email', sortable: true },
  { key: 'nickname', label: 'Nickname', sortable: true },
  { key: 'role', label: 'Role', sortable: true },
  { key: 'enabled', label: 'Enabled', sortable: false },
  { key: 'accountNonLocked', label: 'Locked', sortable: false },
  { key: 'createdAt', label: 'Created', sortable: true },
]

const sortBy = ref('email')
const sortOrder = ref<'asc' | 'desc'>('asc')

// --------------------------------------------------
// LIFECYCLE
// --------------------------------------------------
onMounted(async () => {
  try {
    // Load users
    await adminUserStore.fetchUsers()
  } catch (e) {
    console.error('AdminUsersPage onMounted error', e)
  }
})


// --------------------------------------------------
// HANDLERS
// --------------------------------------------------
function toggleSort(field: string) {
  if (sortBy.value === field) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortBy.value = field
    sortOrder.value = 'asc'
  }
}

async function onChangeRole(userId: string, event: Event) {
  const select = event.target as HTMLSelectElement
  const newRole = select.value as UserRole

  const user = adminUserStore.users.find(u => u.id === userId)
  const oldRole = user?.role

  // Prevent self-demotion to avoid locking the only admin account
  if (currentUserId.value && userId === currentUserId.value && newRole !== 'Moderator') {
    select.value = oldRole as string
    return
  }

  try {
    // ALWAYS bootstrap CSRF before changing role
    await ensureCsrf()

    // Perform role change immediately
    await adminUserStore.changeRole(userId, newRole)
  } catch (e) {
    // Revert UI on error
    select.value = oldRole as string
    console.error('Change role failed', e)
  }
}

// --------------------------------------------------
// HELPERS
// --------------------------------------------------
function formatDate(value: unknown): string {
  if (!value) return '—'

  const date = new Date(value as string)
  return isNaN(date.getTime())
    ? '—'
    : date.toLocaleDateString()
}

function formatEnabled(user: any): string {
  return user.enabled ? 'yes' : 'no'
}

function formatLocked(user: any): string {
  return user.accountNonLocked ? 'no' : 'yes'
}
</script>



<template>
  <div class="list-page">
    <div class="page-card">
      <div class="header-row">
        <h1>Admin · Users</h1>
      </div>

      <!-- Error -->
      <div v-if="adminUserStore.error" class="error">
        {{ adminUserStore.error }}
      </div>

      <!-- Loading -->
      <div v-else-if="adminUserStore.loading" class="loading">
        Loading users...
      </div>

      <!-- Users table -->
      <div v-else>
        <EntityTable
          :columns="tableColumns"
          :rows="adminUserStore.users"
          rowKey="id"
          :sortBy="sortBy"
          :sortOrder="sortOrder"
          @sort="toggleSort"
        >
          <template #th-role>
            <div class="th-label">
              Role
              <span v-if="sortBy === 'role'">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
            </div>
          </template>

          <template #email="{ row }">
            {{ row.email ?? '—' }}
          </template>

          <template #nickname="{ row }">
            {{ row.nickname ?? row.email ?? '—' }}
          </template>

          <template #role="{ row }">
            <select
              :value="row.role"
              :disabled="adminUserStore.loading || (!!currentUserId && row.id === currentUserId)"
              @change="onChangeRole(row.id, $event)"
              @click.stop
            >
              <option value="Applicant">Applicant</option>
              <option value="STUDENT">STUDENT</option>
              <option value="Moderator">Moderator</option>
            </select>
          </template>

          <template #enabled="{ row }">
            {{ formatEnabled(row) }}
          </template>

          <template #accountNonLocked="{ row }">
            {{ formatLocked(row) }}
          </template>

          <template #createdAt="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>

          <template #actions="{ row }">
            <div class="entity-actions">
              <button
                v-if="row.enabled"
                :disabled="adminUserStore.loading"
                @click="adminUserStore.disableUser(row.id)"
              >
                Disable
              </button>

              <button
                v-else
                :disabled="adminUserStore.loading"
                @click="adminUserStore.enableUser(row.id)"
              >
                Enable
              </button>
            </div>
          </template>
        </EntityTable>
      </div>
    </div>
  </div>
</template>
