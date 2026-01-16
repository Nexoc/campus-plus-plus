<template>
  <nav class="navbar">
    <div class="navbar__left">
      <div class="navbar__brand">
        <router-link to="/" class="navbar__logo">
          <img
            src="@/assets/logo.png"
            alt="Campus++ Logo"
            class="navbar__logo-image"
          />
        </router-link>
      </div>

      <div class="navbar__main-links">
        <router-link to="/">Home</router-link>
        <router-link to="/programs">Study Programs</router-link>
        <router-link to="/courses">Courses</router-link>
        <router-link v-if="isAuthenticated" to="/favourites" class="navbar__favourites">
          <StarIcon :size="18" :filled="false" />
          <span>Favourites</span>
        </router-link>
      </div>
    </div>

    <div class="navbar__right">
      <!-- Guest -->
      <template v-if="!isAuthenticated">
        <router-link to="/login">Login</router-link>
        <router-link to="/register">Register</router-link>
      </template>

      <!-- Authenticated -->
      <template v-else>
        <span class="navbar__user">{{ nickname }}</span>

        <router-link to="/account">Account</router-link>

        <router-link v-if="isModerator" to="/moderation/reviews">
          Moderation
        </router-link>

        <router-link v-if="isModerator" to="/admin/users">
          Admin Users
        </router-link>

        <button @click="logout">Logout</button>
      </template>

      <!-- Theme toggle (ALWAYS visible) -->
      <button class="theme-toggle" @click="onToggleTheme">
        {{ theme === 'dark' ? 'Light' : 'Dark' }}
      </button>
    </div>
  </nav>
</template>


<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/modules/auth/store/auth.store'
import { logger } from '@/shared/utils/logger'
import StarIcon from '@/shared/components/icons/StarIcon.vue'

// ⬇️ ВАЖНО: импорт темы
import {
  getTheme,
  toggleTheme,
} from '@/shared/theme/theme'

/* --------------------------------------------------
   AUTH
-------------------------------------------------- */

const authStore = useAuthStore()
const router = useRouter()

const isAuthenticated = computed(() => authStore.isAuthenticated)
const isModerator = computed(() => authStore.isModerator)

const nickname = computed(() => {
  return authStore.user?.nickname || authStore.user?.email
})

const logout = async (): Promise<void> => {
  logger.log('LOGOUT clicked')
  await authStore.logout()
  await router.push('/login')
}

/* --------------------------------------------------
   THEME
-------------------------------------------------- */

const theme = ref(getTheme())

function onToggleTheme(): void {
  theme.value = toggleTheme()
}
</script>
<style scoped>
.navbar__favourites {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.navbar__favourites :deep(.star-icon) {
  margin-top: -2px;
}
</style>