<template>
  <form class="form-container" @submit.prevent="onSubmit">
    <h2>{{ title }}</h2>
    <slot name="fields" :form="form" :errors="errors"></slot>
    <div class="form-actions">
      <button class="base-button" type="submit">{{ submitLabel }}</button>
      <button v-if="showCancel" class="base-button secondary" type="button" @click="$emit('cancel')">Cancel</button>
    </div>
    <div v-if="errors.general" class="error-message">{{ errors.general }}</div>
    <div v-if="success" class="success-message">{{ success }}</div>
  </form>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

const props = defineProps<{
  title: string
  submitLabel?: string
  modelValue: Record<string, any>
  errors?: Record<string, string>
  showCancel?: boolean
  success?: string
}>()

const emit = defineEmits(['update:modelValue', 'submit', 'cancel'])

const form = ref({ ...props.modelValue })
const errors = ref(props.errors || {})
const success = ref(props.success || '')

watch(() => props.modelValue, (val) => {
  form.value = { ...val }
})
watch(() => props.errors, (val) => {
  errors.value = val || {}
})
watch(() => props.success, (val) => {
  success.value = val || ''
})

function onSubmit() {
  emit('submit', form.value)
}
</script>
