<!--
  src/modules/home/pages/HomePage.vue

  Home page for authenticated users.

  Responsibilities:
  - Entry point after login
  - Navigation only
  - Temporary debug action
-->

<template>
  <div class="detail-page">
    <div class="page-card home-page">
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
        <RouterLink to="/programs">
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

    <p v-if="host">
        Site available at:
      <a :href="`http://${host}`" target="_blank" rel="noopener">
        {{ host }}
      </a>

    </p>

    <!-- QR CODE -->
    <img
      v-if="qrDataUrl"
      :src="qrDataUrl"
      alt="QR Code"
      width="220"
      height="220"
    />

  </div>
</template>

<script setup lang="ts">
import QRCode from "qrcode";
import { computed, ref, watchEffect } from "vue";

const host = import.meta.env.VITE_HOST as string | undefined;
const siteUrl = computed(() => (host ? `http://${host}` : ""));

const qrDataUrl = ref<string>("");

watchEffect(async () => {
  if (!siteUrl.value) {
    qrDataUrl.value = "";
    return;
  }

  qrDataUrl.value = await QRCode.toDataURL(siteUrl.value, {
    width: 220,
    margin: 1,
  });
});

</script>

<style scoped>
 .home-page {
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
