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

    <!-- MAIN NAVIGATION BUTTONS -->
    <div class="navigation-buttons">
      <RouterLink to="/study-programs">
        <button class="base-button">
          Study Programs
        </button>
      </RouterLink>

      <RouterLink to="/courses">
        <button class="base-button">
          Courses
        </button>
      </RouterLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'

const auth = useAuthStore()
</script>

<style scoped>
.home-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
  text-align: center;
}

.home-subtitle {
  font-size: 1.1rem;
  color: var(--color-text-secondary);
  margin-bottom: 2rem;
}

.home-content {
  text-align: left;
  margin-bottom: 2rem;
  line-height: 1.6;
}

.navigation-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 2rem;
}

.navigation-buttons .base-button {
  min-width: 200px;
}
</style>
