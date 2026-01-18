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

      <FormError
        v-if="formError"
        :message="formError"
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
// --------------------------------------------------
// Login Page
//
// Purpose:
// - Authenticate user
// - Store JWT via authStore
// - Redirect to home page
//
// Security model:
// - Stateless
// - NO cookies
// - NO CSRF
// --------------------------------------------------

import axios from 'axios'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/modules/auth/store/auth.store'
import { logger } from '@/shared/utils/logger'

import BaseButton from '@/shared/components/BaseButton.vue'
import BaseInput from '@/shared/components/BaseInput.vue'
import FormError from '@/shared/components/FormError.vue'

// --------------------------------------------------
// FORM STATE
// --------------------------------------------------

const email = ref('')
const password = ref('')
const formError = ref<string | null>(null)

// --------------------------------------------------
// DEPENDENCIES
// --------------------------------------------------

const authStore = useAuthStore()
const router = useRouter()

// --------------------------------------------------
// SUBMIT HANDLER
// --------------------------------------------------

const onSubmit = async (): Promise<void> => {
  logger.log('LOGIN submit started')

  formError.value = null

  try {
    await authStore.login(email.value, password.value)
    await router.push('/')
  } catch (error) {
    logger.error('LOGIN failed', error)

    if (axios.isAxiosError(error)) {
      const data = error.response?.data

      if (data?.message) {
        formError.value = data.message
        return
      }
    }

    formError.value = 'Login failed'
  }
}
</script>
