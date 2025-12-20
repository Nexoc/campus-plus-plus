<!--
  src/modules/auth/pages/RegisterPage.vue

  Registration page.
  Responsibilities:
  - render registration form
  - collect user input
  - call auth store registration action
  - display validation and backend errors

  IMPORTANT:
  - This page does NOT log the user in automatically
  - Authentication flow after registration is a UX decision
-->

<template>
  <div class="register-page">
    <h1>Register</h1>

    <form @submit.prevent="onSubmit" class="register-form">
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

      <!-- Confirm password input -->
      <BaseInput
        v-model="confirmPassword"
        type="password"
        label="Confirm Password"
        :error="confirmPasswordError"
        required
      />

      <!-- General form error -->
      <FormError v-if="formError" :message="formError" />

      <!-- Submit button -->
      <BaseButton type="submit" :disabled="isLoading">
        {{ isLoading ? 'Registering…' : 'Register' }}
      </BaseButton>
    </form>
  </div>
</template>

<script setup lang="ts">
/*
  Script setup block.

  This component:
  - keeps only UI state
  - performs basic client-side validation
  - delegates business logic to auth store
*/

import { AxiosError } from 'axios'
import { computed, ref } from 'vue'
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
const confirmPassword = ref('')

const isLoading = ref(false)
const formError = ref<string | null>(null)

// --------------------------------------------------
// Form error handling (field-level)
// --------------------------------------------------
const { fieldErrors, setErrors, clearErrors } = useFormErrors()

// --------------------------------------------------
// Confirm password validation
// --------------------------------------------------
const confirmPasswordError = computed(() => {
  if (!confirmPassword.value) {
    return undefined
  }

  if (confirmPassword.value !== password.value) {
    return 'Passwords do not match'
  }

  return undefined
})

// --------------------------------------------------
// Dependencies
// --------------------------------------------------
const authStore = useAuthStore()
const router = useRouter()

// --------------------------------------------------
// Form submit handler
// --------------------------------------------------
const onSubmit = async () => {
  // Reset previous errors
  clearErrors()
  formError.value = null

  // Client-side validation
  if (confirmPassword.value !== password.value) {
    formError.value = 'Passwords do not match'
    return
  }

  isLoading.value = true

  try {
    logger.log('[register-page] registration submit')

    // Delegate registration to the store
    await authStore.register(email.value, password.value)

    // After successful registration, redirect to login page
    await router.push({ name: 'login' })
  } catch (error) {
    logger.warn('[register-page] registration failed', error)

    if (error instanceof AxiosError && error.response) {
      const status = error.response.status

      // Backend validation errors (400)
      if (status === 400 && error.response.data?.fieldErrors) {
        setErrors(error.response.data.fieldErrors)
      }
      // Email already exists (409)
      else if (status === 409) {
        formError.value = 'Email already exists'
      }
      // Fallback error
      else {
        formError.value = 'Registration failed. Please try again.'
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
*/

.register-page {
  max-width: 400px;
  margin: 0 auto;
  padding: 2rem;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
</style>
