<template>
  <div class="change-password-page">
    <h1>Change Password</h1>

    <form @submit.prevent="onSubmit">
      <!-- Current password -->
      <BaseInput
        v-model="currentPassword"
        label="Current password"
        :type="showCurrent ? 'text' : 'password'"
      />

      <BaseButton
        type="button"
        @click="showCurrent = !showCurrent"
      >
        {{ showCurrent ? 'Hide' : 'Show' }}
      </BaseButton>

      <!-- New password -->
      <BaseInput
        v-model="newPassword"
        label="New password"
        :type="showNew ? 'text' : 'password'"
      />

      <BaseButton
        type="button"
        @click="showNew = !showNew"
      >
        {{ showNew ? 'Hide' : 'Show' }}
      </BaseButton>

      <!-- Confirm new password -->
      <BaseInput
        v-model="confirmPassword"
        label="Confirm new password"
        :type="showConfirm ? 'text' : 'password'"
      />

      <BaseButton
        type="button"
        @click="showConfirm = !showConfirm"
      >
        {{ showConfirm ? 'Hide' : 'Show' }}
      </BaseButton>

      <!-- Form error -->
      <FormError
        v-if="formError"
        :message="formError"
      />

      <!-- Actions -->
      <div class="actions">
        <BaseButton type="submit">
          Change password
        </BaseButton>

        <BaseButton
          type="button"
          @click="goHome"
        >
          Back to home
        </BaseButton>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
// --------------------------------------------------
// Change Password Page
//
// Responsibilities:
// - Allow authenticated user to change password
// - Perform basic client-side validation
// - Delegate business logic to account store
// --------------------------------------------------

import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { useAccountStore } from '@/modules/account/store/account.store'
import { logger } from '@/shared/utils/logger'

import BaseButton from '@/shared/components/BaseButton.vue'
import BaseInput from '@/shared/components/BaseInput.vue'
import FormError from '@/shared/components/FormError.vue'

// --------------------------------------------------
// FORM STATE
// --------------------------------------------------

const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const formError = ref<string | null>(null)

// --------------------------------------------------
// VISIBILITY STATE
// --------------------------------------------------

const showCurrent = ref(false)
const showNew = ref(false)
const showConfirm = ref(false)

// --------------------------------------------------
// DEPENDENCIES
// --------------------------------------------------

const accountStore = useAccountStore()
const router = useRouter()

// --------------------------------------------------
// SUBMIT HANDLER
// --------------------------------------------------

const onSubmit = async (): Promise<void> => {
  formError.value = null

  if (newPassword.value !== confirmPassword.value) {
    formError.value = 'Passwords do not match'
    logger.warn('CHANGE PASSWORD validation failed')
    return
  }

  try {
    logger.log('CHANGE PASSWORD request started')

    await accountStore.changePassword(
      currentPassword.value,
      newPassword.value
    )

    logger.log('CHANGE PASSWORD successful')

    // UX cleanup
    currentPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
  } catch (error) {
    logger.error('CHANGE PASSWORD failed', error)
    formError.value = 'Failed to change password'
  }
}

// --------------------------------------------------
// NAVIGATION
// --------------------------------------------------

const goHome = async (): Promise<void> => {
  await router.push({ name: 'home' })
}
</script>
