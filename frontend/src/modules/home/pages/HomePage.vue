<!--
  src/modules/home/pages/HomePage.vue

  Home page for authenticated users.
-->

<template>
  <div class="home-page">
    <h1>Welcome</h1>

    <p class="home-subtitle">
      You are successfully logged in.
    </p>

    <div class="home-actions">
      <!-- Admin button (visible only for admins) -->
      <BaseButton
        v-if="authStore.isAdmin"
        @click="goToAdmin"
      >
        Admin panel
      </BaseButton>

      <!-- Change password -->
      <BaseButton @click="goToChangePassword">
        Change password
      </BaseButton>

      <!-- Logout -->
      <BaseButton @click="onLogout">
        Logout
      </BaseButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import BaseButton from '@/shared/components/BaseButton.vue'
import { logger } from '@/shared/utils/logger'
import { useRouter } from 'vue-router'

// --------------------------------------------------
// STORE & ROUTER
// --------------------------------------------------
const authStore = useAuthStore()
const router = useRouter()

// --------------------------------------------------
// NAVIGATION
// --------------------------------------------------
const goToAdmin = async () => {
  logger.log('[home-page] admin panel clicked')
  await router.push({ name: 'AdminUsers' })
}

const goToChangePassword = async () => {
  logger.log('[home-page] change password clicked')
  await router.push({ name: 'ChangePassword' })
}

const onLogout = async () => {
  logger.log('[home-page] logout clicked')

  authStore.logout()
  await router.push({ name: 'login' })
}
</script>
