<template>
  <div class="change-password-page">
    <h1>Change Password (DEBUG)</h1>

    <form @submit.prevent="onSubmit">
      <!-- Current password -->
      <div class="password-field">
        <input
          v-model="currentPassword"
          :type="showCurrent ? 'text' : 'password'"
          placeholder="current password"
        />
        <button
          type="button"
          class="eye-btn"
          @click="showCurrent = !showCurrent"
        >
          {{ showCurrent ? '🙈' : '👁️' }}
        </button>
      </div>

      <!-- New password -->
      <div class="password-field">
        <input
          v-model="newPassword"
          :type="showNew ? 'text' : 'password'"
          placeholder="new password"
        />
        <button
          type="button"
          class="eye-btn"
          @click="showNew = !showNew"
        >
          {{ showNew ? '🙈' : '👁️' }}
        </button>
      </div>

      <!-- Confirm password -->
      <div class="password-field">
        <input
          v-model="confirmPassword"
          :type="showConfirm ? 'text' : 'password'"
          placeholder="confirm new password"
        />
        <button
          type="button"
          class="eye-btn"
          @click="showConfirm = !showConfirm"
        >
          {{ showConfirm ? '🙈' : '👁️' }}
        </button>
      </div>

      <div class="actions">
        <button type="submit">Change password</button>

        <!-- Back to home -->
        <button type="button" @click="goHome">
          Back to home
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { useAccountStore } from '@/modules/account/store/account.store'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

// --------------------------------------------------
// FORM STATE
// --------------------------------------------------
const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

// --------------------------------------------------
// VISIBILITY STATE
// --------------------------------------------------
const showCurrent = ref(false)
const showNew = ref(false)
const showConfirm = ref(false)

// --------------------------------------------------
// STORE & ROUTER
// --------------------------------------------------
const accountStore = useAccountStore()
const router = useRouter()

// --------------------------------------------------
// SUBMIT HANDLER
// --------------------------------------------------
const onSubmit = async () => {
  console.group('[CHANGE PASSWORD]')

  if (newPassword.value !== confirmPassword.value) {
    console.error('Passwords do not match')
    console.groupEnd()
    return
  }

  try {
    console.log('[CHANGE PASSWORD] sending POST /account/change-password')

    await accountStore.changePassword(
      currentPassword.value,
      newPassword.value
    )

    console.log('[CHANGE PASSWORD] success')

    // UX cleanup
    currentPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
  } catch (error) {
    console.error('[CHANGE PASSWORD ERROR]', error)
  } finally {
    console.groupEnd()
  }
}

// --------------------------------------------------
// NAVIGATION
// --------------------------------------------------
const goHome = async () => {
  await router.push({ name: 'home' })
}
</script>

<style scoped>
.password-field {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.password-field input {
  flex: 1;
}

.eye-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.2rem;
}

.actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}
</style>
