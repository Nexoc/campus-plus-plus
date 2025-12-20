<template>
  <div class="login-page">
    <h1>Login (DEBUG)</h1>

    <form @submit.prevent="onSubmit">
      <input v-model="email" placeholder="email" />
      <input v-model="password" type="password" placeholder="password" />

      <button type="submit">Login</button>
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

// --------------------------------------------------
// DEPENDENCIES
// --------------------------------------------------
const authStore = useAuthStore()
const router = useRouter()

// --------------------------------------------------
// SUBMIT HANDLER
// --------------------------------------------------
const onSubmit = async () => {
  console.group('[LOGIN SUBMIT]')

  console.log('Email:', email.value)
  console.log(
    'Browser cookies (should NOT be used for login):',
    document.cookie
  )

  try {
    // --------------------------------------------------
    // Login request
    //
    // Security notes:
    // - NO cookies
    // - NO CSRF
    // - JWT is returned in response body
    // --------------------------------------------------
    console.log('[LOGIN] sending POST /auth/login')

    await authStore.login(email.value, password.value)

    console.log('[LOGIN] success -> redirect home')
    await router.push('/')
  } catch (error) {
    console.error('[LOGIN ERROR]', error)
  } finally {
    console.groupEnd()
  }
}
</script>
