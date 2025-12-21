<template>
  <div class="account-page">
    <h1>Account</h1>

    <!-- User info -->
    <div class="account-card">
      <div class="account-row">
        <span class="account-label">Email</span>
        <span class="account-value">{{ user.email }}</span>
      </div>

      <div class="account-row">
        <span class="account-label">Nickname</span>
        <span class="account-value">
          {{ user.nickname || '—' }}
        </span>
      </div>
    </div>

    <!-- Actions -->
    <div class="account-actions">
      <router-link
        class="base-button"
        to="/account/change-password"
      >
        Change password
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { computed } from 'vue'

// --------------------------------------------------
// STORE
// --------------------------------------------------
const authStore = useAuthStore()

// --------------------------------------------------
// DERIVED USER DATA
// --------------------------------------------------
// Router guard already guarantees authentication,
// so user MUST exist here.
const user = computed(() => {
  if (!authStore.user) {
    throw new Error('AccountPage loaded without authenticated user')
  }
  return authStore.user
})
</script>

<style scoped>
.account-page {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
}

.account-card {
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 1.5rem;
  margin-bottom: 2rem;
}

.account-row {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
}

.account-label {
  font-weight: 600;
  color: #374151;
}

.account-value {
  color: #111827;
}

.account-actions {
  display: flex;
  gap: 1rem;
}
</style>
