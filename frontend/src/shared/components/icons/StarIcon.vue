<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    :class="['star-icon', { filled }]"
  >
    <path
      d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z"
      :stroke="filled ? 'none' : 'currentColor'"
      :fill="filled ? 'currentColor' : 'none'"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
    />
  </svg>
</template>

<script setup lang="ts">
defineProps<{
  size?: number
  filled?: boolean
}>()
</script>

<style scoped>
.star-icon {
  display: inline-block;
  vertical-align: middle;
  transition: all 0.2s ease;
}

.star-icon:not(.filled) {
  color: var(--text-color, #333);
}

.star-icon.filled {
  color: var(--star-color, #fbbf24);
}
</style>
