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
    </div>

    <button class="navbar__burger" :class="{ open: menuOpen }" @click="toggleMenu">
      <span></span>
      <span></span>
      <span></span>
    </button>

    <div class="navbar__main-links" :class="{ open: menuOpen }">
      <router-link to="/" @click="menuOpen = false">Home</router-link>
      <router-link to="/programs" @click="menuOpen = false">Study Programs</router-link>
      <router-link to="/courses" @click="menuOpen = false">Courses</router-link>
      <router-link v-if="isAuthenticated" to="/favourites" @click="menuOpen = false">
        <StarIcon :size="18" :filled="false" />
        <span>Favourites</span>
      </router-link>
    </div>

    <div class="navbar__right" :class="{ open: menuOpen }">
      <!-- Guest -->
      <template v-if="!isAuthenticated">
        <router-link to="/login">Login</router-link>
        <router-link to="/register">Register</router-link>
      </template>

      <!-- Authenticated -->
      <template v-else>
        <span class="navbar__user">{{ nickname }}</span>

        <router-link to="/account">Account</router-link>

        <router-link v-if="isModerator" to="/moderation/reports" class="navbar__moderation">
          Moderation
          <span v-if="pendingReportsCount > 0" class="report-badge">
            {{ pendingReportsCount }}
          </span>
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
import { computed, ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

import { useAuthStore } from '@/modules/auth/store/auth.store'
import { logger } from '@/shared/utils/logger'
import StarIcon from '@/shared/components/icons/StarIcon.vue'
import { moderationApi } from '@/modules/moderation/api/moderationApi'

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
const route = useRoute()

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

/* --------------------------------------------------
   MOBILE MENU
-------------------------------------------------- */

const menuOpen = ref(false)

function toggleMenu(): void {
  menuOpen.value = !menuOpen.value
}

// Close menu when route changes
watch(() => route.path, () => {
  menuOpen.value = false
})

/* --------------------------------------------------
   PENDING REPORTS COUNT (FR-S-4)
-------------------------------------------------- */

const pendingReportsCount = ref(0)

async function loadPendingCount() {
  if (!isModerator.value) return
  
  try {
    pendingReportsCount.value = await moderationApi.getPendingCount()
  } catch (error) {
    // Silently fail - don't break navigation
    console.error('Failed to load pending reports count:', error)
  }
}

// Load count when mounted if user is moderator
onMounted(() => {
  if (isModerator.value) {
    loadPendingCount()
  }
})

// Reload count when moderator status changes
watch(isModerator, (newValue) => {
  if (newValue) {
    loadPendingCount()
  } else {
    pendingReportsCount.value = 0
  }
})

// Reload count when route changes (to refresh after resolving reports)
watch(() => route.path, () => {
  if (isModerator.value) {
    loadPendingCount()
  }
})
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

.navbar__moderation {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.report-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  background: #ff4444;
  color: #ffffff;
  font-size: 12px;
  font-weight: 600;
  border-radius: 10px;
  line-height: 1;
}
</style>