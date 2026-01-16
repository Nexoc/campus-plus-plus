<!--
  src/shared/components/BaseButton.vue

  Reusable base button component.
  Provides consistent styling and behavior
  for buttons across the application.
-->

<template>
  <button
    class="base-button"
    :type="type"
    :disabled="disabled"
  >
    <slot></slot>
  </button>
</template>

<script setup lang="ts">
/*
  Props definition.

  This component is intentionally minimal.
  All business logic should live outside.
*/

defineProps<{
  type?: 'button' | 'submit' | 'reset'
  disabled?: boolean
}>()
</script>
