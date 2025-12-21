<template>
  <nav class="navbar">
    <div class="navbar__brand">Campus++</div>

    <div class="navbar__links">
      <!-- Guest -->
      <template v-if="!isAuthenticated">
        <router-link to="/login">Login</router-link>
        <router-link to="/register">Register</router-link>
      </template>

      <!-- Authenticated -->
      <template v-else>
        <span class="navbar__user">{{ nickname }}</span>

        <router-link to="/">Home</router-link>
        <router-link to="/account">Account</router-link>

        <router-link v-if="isAdmin" to="/admin/users">
          Admin
        </router-link>

        <button @click="logout">Logout</button>
      </template>
    </div>
  </nav>
</template>



<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/modules/auth/store/auth.store'
import { logger } from '@/shared/utils/logger'

const authStore = useAuthStore()
const router = useRouter()

const isAuthenticated = computed(() => authStore.isAuthenticated)
const isAdmin = computed(() => authStore.isAdmin)

// Prefer nickname, fallback to email
const nickname = computed(() => {
  return authStore.user?.nickname || authStore.user?.email
})

const logout = async (): Promise<void> => {
  logger.log('LOGOUT clicked')
  await authStore.logout()
  await router.push('/login')
}
</script>
