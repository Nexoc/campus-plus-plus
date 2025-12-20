<!--
  src/modules/auth/pages/LoginPage.vue

  Login page.
  Responsibilities:
  - render login form
  - collect user credentials
  - call auth store login action
  - display validation / authentication errors
  - provide navigation to registration page

  IMPORTANT:
  - This page does NOT talk to axios directly
  - This page does NOT manage JWT tokens
  - This page does NOT handle global errors
-->

<template>
  <div class="login-page">
    <h1>Login</h1>

    <form @submit.prevent="onSubmit" class="login-form">
      <!-- Email input -->
      <BaseInput
        v-model="email"
        type="email"
        label="Email"
        :error="fieldErrors.email"
        required
      />

      <!-- Password input -->
      <BaseInput
        v-model="password"
        type="password"
        label="Password"
        :error="fieldErrors.password"
        required
      />

      <!-- General form error (e.g. invalid credentials) -->
      <FormError v-if="formError" :message="formError" />

      <!-- Submit button -->
      <BaseButton type="submit" :disabled="isLoading">
        {{ isLoading ? 'Logging in…' : 'Login' }}
      </BaseButton>
    </form>

    <!-- Register link -->
    <p class="auth-link">
      Don’t have an account?
      <router-link to="/register">Register</router-link>
    </p>
  </div>
</template>

<script setup lang="ts">
/*
  Script setup block.

  This component:
  - keeps only UI state
  - delegates business logic to auth store
  - handles backend errors and maps them to the UI
*/

import { AxiosError } from 'axios'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/modules/auth/store/auth.store'
import { useFormErrors } from '@/shared/composables/useFormErrors'
import { logger } from '@/shared/utils/logger'

// Shared UI components
import BaseButton from '@/shared/components/BaseButton.vue'
import BaseInput from '@/shared/components/BaseInput.vue'
import FormError from '@/shared/components/FormError.vue'

// --------------------------------------------------
// Local reactive state
// --------------------------------------------------
const email = ref('')
const password = ref('')
const isLoading = ref(false)
const formError = ref<string | null>(null)

// --------------------------------------------------
// Form error handling (field-level)
// --------------------------------------------------
const { fieldErrors, setErrors, clearErrors } = useFormErrors()

// --------------------------------------------------
// Dependencies
// --------------------------------------------------
const authStore = useAuthStore()
const router = useRouter()

// --------------------------------------------------
// Form submit handler
// --------------------------------------------------
const onSubmit = async () => {
  clearErrors()
  formError.value = null
  isLoading.value = true

  try {
    logger.log('[login-page] login submit')

    await authStore.login(email.value, password.value)

    // Redirect to home page after successful login
    await router.push({ name: 'home' })
  } catch (error) {
    logger.warn('[login-page] login failed', error)

    if (error instanceof AxiosError && error.response) {
      const status = error.response.status

      if (status === 400 && error.response.data?.fieldErrors) {
        setErrors(error.response.data.fieldErrors)
      } else if (status === 401) {
        formError.value = 'Invalid email or password'
      } else {
        formError.value = 'Login failed. Please try again.'
      }
    } else {
      formError.value = 'Unexpected error occurred.'
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
/*
  Basic page layout styles.
  These styles are intentionally minimal.
  Layout / theme styles should live in main.css.
*/

.login-page {
  max-width: 400px;
  margin: 0 auto;
  padding: 2rem;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.auth-link {
  margin-top: 1rem;
  text-align: center;
}

.auth-link a {
  color: #2563eb;
  text-decoration: none;
  font-weight: 500;
}

.auth-link a:hover {
  text-decoration: underline;
}
</style>
