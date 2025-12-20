<!--
  src/modules/home/pages/HomePage.vue

  Home page for authenticated users.
  Styling is handled globally in main.css.
-->

<template>
  <div class="home-page">
    <h1>Welcome</h1>

    <p class="home-subtitle">
      You are successfully logged in.
    </p>

    <div class="home-actions">
      <BaseButton @click="onLogout">
        Logout
      </BaseButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/modules/auth/store/auth.store'
import BaseButton from '@/shared/components/BaseButton.vue'
import { logger } from '@/shared/utils/logger'

const authStore = useAuthStore()
const router = useRouter()

const onLogout = async () => {
  logger.log('[home-page] logout clicked')

  authStore.logout()
  await router.push({ name: 'login' })
}
</script>
