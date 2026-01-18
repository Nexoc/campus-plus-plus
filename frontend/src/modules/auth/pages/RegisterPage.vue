<template>
  <div class="register-page">
    <h1>Register</h1>

    <form @submit.prevent="onSubmit">
      <BaseInput
        v-model="email"
        label="Email"
        type="email"
        :error="fieldErrors.email"
      />

      <BaseInput
        v-model="nickname"
        label="Nickname"
        :error="fieldErrors.nickname"
      />

      <BaseInput
        v-model="password"
        label="Password"
        type="password"
        :error="fieldErrors.password"
      />

      <BaseInput
        v-model="confirmPassword"
        label="Confirm password"
        type="password"
      />

      <FormError
        v-if="formError"
        :message="formError"
      />

      <BaseButton type="submit">
        Register
      </BaseButton>
    </form>

    <!-- Auth navigation -->
    <div class="auth-link">
      <router-link to="/login">
        Already have an account? Login
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
// --------------------------------------------------
// Register Page
//
// Purpose:
// - Create new user account
// - Perform basic client-side validation
// - Redirect to login page after successful registration
//
// Security model:
// - Stateless
// - NO cookies
// - NO CSRF
// --------------------------------------------------

import axios from 'axios'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { logger } from '@/shared/utils/logger'

import BaseButton from '@/shared/components/BaseButton.vue'
import BaseInput from '@/shared/components/BaseInput.vue'
import FormError from '@/shared/components/FormError.vue'

import * as authApi from '@/modules/auth/api/auth.api'
import { useFormErrors } from '@/shared/composables/useFormErrors'

// --------------------------------------------------
// FORM STATE
// --------------------------------------------------

const email = ref('')
const nickname = ref('')
const password = ref('')
const confirmPassword = ref('')
const formError = ref<string | null>(null)

const { fieldErrors, setErrors, clearErrors } = useFormErrors()

// --------------------------------------------------
// DEPENDENCIES
// --------------------------------------------------

const router = useRouter()

// --------------------------------------------------
// SUBMIT HANDLER
// --------------------------------------------------

const onSubmit = async (): Promise<void> => {
  logger.log('REGISTER submit started', { email: email.value })

  formError.value = null
  clearErrors()

  // --------------------------------------------------
  // Client-side validation
  // --------------------------------------------------
  if (password.value !== confirmPassword.value) {
    formError.value = 'Passwords do not match'
    logger.warn('REGISTER validation failed: passwords do not match')
    return
  }

  try {
    // --------------------------------------------------
    // Register request
    //
    // Security notes:
    // - Pure JSON POST
    // - NO cookies
    // - NO CSRF
    // --------------------------------------------------
    await authApi.register({
      email: email.value,
      password: password.value,
      nickname: nickname.value,
    })

    logger.log('REGISTER successful, redirecting to login')

    await router.push('/login')
  } catch (error) {
    logger.error('REGISTER failed', error)

    if (axios.isAxiosError(error)) {
      const data = error.response?.data

      // field validation errors (e.g. password < 8, nickname missing)
      if (data?.fieldErrors) {
        setErrors(data.fieldErrors)
        return
      }

      // fallback business error
      if (data?.message) {
        formError.value = data.message
        return
      }
    }

    formError.value = 'Registration failed'
  }
}
</script>
