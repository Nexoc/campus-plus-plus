<!--
  FormError.vue

  Displays a general form-level error message.
-->

<template>
  <div class="form-error">
    {{ message }}
  </div>
</template>

<script setup lang="ts">
defineProps<{
  message: string
}>()
</script>
