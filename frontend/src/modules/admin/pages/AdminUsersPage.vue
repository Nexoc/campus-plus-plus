<script setup lang="ts">
import { ensureCsrf } from '@/app/security/csrf'
import type { UserRole } from '@/modules/admin/api/admin-user.api'
import { useAdminUserStore } from '@/modules/admin/store/admin-user.store'
import { onMounted } from 'vue'


const adminUserStore = useAdminUserStore()

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
async function onChangeRole(userId: string, event: Event) {
  const select = event.target as HTMLSelectElement
  const newRole = select.value as UserRole

  const user = adminUserStore.users.find(u => u.id === userId)
  const oldRole = user?.role

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
</script>


<template>
  <section>
    <h1>Admin · Users</h1>

    <!-- Error -->
    <div v-if="adminUserStore.error" class="error">
      {{ adminUserStore.error }}
    </div>

    <!-- Loading -->
    <div v-if="adminUserStore.loading">
      Loading users...
    </div>

    <!-- Users table -->
    <table v-else class="users-table">
      <thead>
        <tr>
          <th>Email</th>
          <th>Nickname</th>
          <th>Role</th>
          <th>Enabled</th>
          <th>Locked</th>
          <th>Created</th>
          <th>Actions</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="user in adminUserStore.users" :key="user.id">
          <!-- Email -->
          <td>{{ user.email ?? '—' }}</td>

          <!-- Nickname -->
          <td>
            {{ user.nickname ?? user.email ?? '—' }}
          </td>

          <!-- Role -->
          <td>
            <select
              :value="user.role"
              :disabled="adminUserStore.loading"
              @change="onChangeRole(user.id, $event)"
            >
              <option value="VISITOR">VISITOR</option>
              <option value="STUDENT">STUDENT</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </td>

          <!-- Enabled -->
          <td>{{ user.enabled ? 'yes' : 'no' }}</td>

          <!-- Locked -->
          <td>{{ user.accountNonLocked ? 'no' : 'yes' }}</td>

          <!-- Created -->
          <td>{{ formatDate(user.createdAt) }}</td>

          <!-- Actions -->
          <td>
            <button
              v-if="user.enabled"
              :disabled="adminUserStore.loading"
              @click="adminUserStore.disableUser(user.id)"
            >
              Disable
            </button>

            <button
              v-else
              :disabled="adminUserStore.loading"
              @click="adminUserStore.enableUser(user.id)"
            >
              Enable
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<style scoped>
.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th,
.users-table td {
  padding: 0.5rem;
  border: 1px solid #ccc;
  text-align: left;
}

.error {
  margin-bottom: 1rem;
  color: #b00020;
}
</style>
