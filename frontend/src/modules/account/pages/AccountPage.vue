<template>
  <div class="detail-page">
    <div class="page-card account-page">
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
