<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import NavBar from '@/shared/components/NavBar.vue'
import { onMounted } from 'vue'

const authStore = useAuthStore()

onMounted(() => {
  authStore.init()
})
</script>

<template>
  <NavBar />
  <router-view />
</template>
