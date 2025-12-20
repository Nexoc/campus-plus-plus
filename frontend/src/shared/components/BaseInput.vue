<!--
  BaseInput.vue

  Reusable input component with:
  - label
  - v-model support
  - error display
-->

<template>
  <div class="base-input">
    <label v-if="label" class="base-input__label">
      {{ label }}
    </label>

    <input
      class="base-input__field"
      :type="type"
      :value="modelValue"
      :required="required"
      @input="onInput"
    />

    <p v-if="error" class="base-input__error">
      {{ error }}
    </p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  modelValue: string
  type?: string
  label?: string
  error?: string
  required?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const onInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}
</script>
