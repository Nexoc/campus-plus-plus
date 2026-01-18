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

      <div v-if="host" class="site-info">
        <p>
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
    </div>

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
.detail-page {
  padding: var(--container-padding);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  width: 100%;
  box-sizing: border-box;
  min-height: calc(100vh - var(--navbar-height));
}

.page-card {
  width: 100%;
  max-width: 900px;
  background: var(--color-surface);
  border-radius: var(--radius-md);
  padding: var(--space-2xl);
  box-shadow: var(--shadow-md);
  color: var(--color-text);
  box-sizing: border-box;
}

@media (max-width: 639px) {
  .page-card {
    padding: var(--space-lg);
    margin: var(--space-lg) auto;
  }
}

.home-page {
  text-align: center;
}

.page-card h1 {
  margin: 0 0 var(--space-lg) 0;
  font-size: var(--font-2xl);
  color: var(--color-text);
  font-weight: 700;
}

@media (max-width: 639px) {
  .page-card h1 {
    font-size: var(--font-xl);
  }
}

.home-subtitle {
  font-size: 1.125rem;
  color: var(--color-primary);
  margin-bottom: var(--space-xl);
  font-weight: 600;
}

.home-content {
  text-align: left;
  margin-bottom: var(--space-2xl);
  line-height: 1.7;
  background: var(--color-primary-light);
  padding: var(--space-lg);
  border-radius: var(--radius-md);
  border-left: 4px solid var(--color-primary);
  color: var(--color-text);
}

.home-content p {
  margin: var(--space-md) 0;
  font-size: var(--font-base);
}

.navigation-buttons {
  display: flex;
  gap: var(--space-lg);
  justify-content: center;
  flex-wrap: wrap;
  margin-top: var(--space-2xl);
  margin-bottom: var(--space-2xl);
}

.navigation-buttons a {
  text-decoration: none;
  display: inline-block;
}

.navigation-buttons .base-button {
  min-width: 200px;
  font-size: var(--font-base);
  font-weight: 700;
  padding: var(--space-md) var(--space-lg);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.navigation-buttons .base-button:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.site-info {
  margin-top: var(--space-2xl);
  padding-top: var(--space-xl);
  border-top: 2px solid var(--color-border);
  text-align: center;
}

.site-info p {
  margin-bottom: var(--space-md);
  color: var(--color-text-muted);
  font-size: var(--font-sm);
}

.site-info a {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s;
}

.site-info a:hover {
  color: var(--color-primary-hover);
  text-decoration: underline;
}

.site-info img {
  margin-top: var(--space-lg);
  border: 2px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: var(--space-md);
  background: var(--color-surface);
}
</style>
