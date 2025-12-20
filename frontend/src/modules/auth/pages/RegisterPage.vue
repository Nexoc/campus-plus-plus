<template>
  <div class="register-page">
    <h1>Register (DEBUG)</h1>

    <form @submit.prevent="onSubmit">
      <input v-model="email" placeholder="email" />
      <input v-model="password" type="password" placeholder="password" />
      <input
        v-model="confirmPassword"
        type="password"
        placeholder="confirm password"
      />

      <button type="submit">Register</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/store/auth.store'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

// --------------------------------------------------
// FORM STATE
// --------------------------------------------------
const email = ref('')
const password = ref('')
const confirmPassword = ref('')

// --------------------------------------------------
// DEPENDENCIES
// --------------------------------------------------
const authStore = useAuthStore()
const router = useRouter()

// --------------------------------------------------
// SUBMIT HANDLER
// --------------------------------------------------
const onSubmit = async () => {
  console.group('[REGISTER SUBMIT]')

  console.log('Email:', email.value)
  console.log('Browser cookies (should be irrelevant):', document.cookie)

  // Client-side validation
  if (password.value !== confirmPassword.value) {
    console.error('Passwords do not match')
    console.groupEnd()
    return
  }

  try {
    // --------------------------------------------------
    // Register request
    //
    // Security notes:
    // - NO cookies
    // - NO CSRF
    // - Pure JSON POST
    // --------------------------------------------------
    console.log('[REGISTER] sending POST /auth/register')

    await authStore.register(email.value, password.value)

    console.log('[REGISTER] success -> redirect to login')
    await router.push('/login')
  } catch (error) {
    console.error('[REGISTER ERROR]', error)
  } finally {
    console.groupEnd()
  }
}
</script>
