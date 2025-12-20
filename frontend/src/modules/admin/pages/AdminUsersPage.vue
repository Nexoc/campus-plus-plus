<template>
  <div class="admin-users-page">
    <h1>Admin – User Management (DEBUG)</h1>

    <div class="admin-actions">
      <input v-model.number="userId" placeholder="user id" />

      <select v-model="role">
        <option value="STUDENT">STUDENT</option>
        <option value="ADMIN">ADMIN</option>
      </select>

      <button @click="onChangeRole">
        Change role
      </button>

      <button @click="onDisable">
        Disable user
      </button>

      <button @click="onEnable">
        Enable user
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAdminUserStore } from '@/modules/admin/store/admin-user.store'
import { ref } from 'vue'

const adminStore = useAdminUserStore()

const userId = ref<number | null>(null)
const role = ref('STUDENT')

const onChangeRole = async () => {
  if (!userId.value) return
  await adminStore.changeRole(userId.value, role.value)
}

const onDisable = async () => {
  if (!userId.value) return
  await adminStore.disableUser(userId.value)
}

const onEnable = async () => {
  if (!userId.value) return
  await adminStore.enableUser(userId.value)
}
</script>
