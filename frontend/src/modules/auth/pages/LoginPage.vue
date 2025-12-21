<template>
  <div class="login-page">
    <h1>Login</h1>

    <form @submit.prevent="onSubmit">
      <BaseInput
        v-model="email"
        label="Email"
        type="email"
      />

      <BaseInput
        v-model="password"
        label="Password"
        type="password"
      />

      <BaseButton type="submit">
        Login
      </BaseButton>
    </form>

    <!-- Auth navigation -->
    <div class="auth-link">
      <router-link to="/register">
        Don’t have an account? Register
      </router-link>
    </div>
  </div>
</template>


<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/modules/auth/store/auth.store'
import { logger } from '@/shared/utils/logger'

import BaseButton from '@/shared/components/BaseButton.vue'
import BaseInput from '@/shared/components/BaseInput.vue'

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

const onSubmit = async (): Promise<void> => {
  logger.log('LOGIN submit')

  try {
    await authStore.login(email.value, password.value)
    await router.push('/')
  } catch (error) {
    logger.error('LOGIN failed', error)
  }
}
</script>
