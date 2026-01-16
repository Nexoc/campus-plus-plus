<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import NavBar from '@/shared/components/NavBar.vue'
import ToastNotification from '@/shared/components/ToastNotification.vue'
import { setToastInstance } from '@/shared/composables/useToast'
import { onMounted, ref, nextTick } from 'vue'

const authStore = useAuthStore()
const toastRef = ref()

onMounted(async () => {
  authStore.init()
  
  // Set up global toast instance after next tick
  await nextTick()
  if (toastRef.value) {
    setToastInstance(toastRef.value)
  }
})
</script>

<template>
  <NavBar />
  <router-view />
  <ToastNotification ref="toastRef" />
</template>
