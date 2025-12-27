<!--
  src/modules/home/pages/HomePage.vue

  Home page for authenticated users.

  Responsibilities:
  - Entry point after login
  - Navigation only
  - Temporary debug action
-->

<template>
  <div class="home-page">
    <h1>Welcome</h1>

    <p class="home-subtitle">
      You are on <strong>Campus++</strong>.
    </p>

    <div class="home-content">
      <p>
        This platform provides a secure environment for managing users,
        roles, and access within the system.
      </p>

      <p>
        Use the navigation bar above to move between sections.
        Your access level determines which features are available to you.
      </p>
    </div>

    <!-- MAIN ENTRY POINT -->
    <RouterLink to="/courses">
      <button class="base-button">
        Browse courses
      </button>
    </RouterLink>

    <!-- TEMP DEBUG -->
    <hr />

    <button
      class="base-button"
      :disabled="!auth.isAuthenticated"
      @click="debugMe"
    >
      Debug: /api/debug/me
    </button>


    <pre v-if="debugResult">{{ debugResult }}</pre>
    <p v-if="debugError" class="error">{{ debugError }}</p>


  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { getDebugMe } from '@/modules/home/api/debugApi'
import { ref } from 'vue'

const auth = useAuthStore()
const debugResult = ref<string | null>(null)
const debugError = ref<string | null>(null)

async function debugMe() {
  debugResult.value = null
  debugError.value = null

  try {
    const data = await getDebugMe()
    debugResult.value = JSON.stringify(data, null, 2)
  } catch (e: any) {
    debugError.value =
      e?.response?.status + ' ' + e?.response?.statusText
  }
}
</script>
