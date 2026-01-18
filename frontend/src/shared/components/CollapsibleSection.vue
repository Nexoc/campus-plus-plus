<template>
  <section class="collapsible-section">
    <div class="section-header">
      <h3>{{ title }}</h3>
      <slot name="header-extra"></slot>
    </div>
    
    <div v-if="$slots['summary']" class="section-summary">
      <slot name="summary"></slot>
    </div>

    <!-- Collapsible Form Area -->
    <div v-if="showToggle" class="collapsible-form-area">
      <button 
        class="toggle-button"
        @click="isFormVisible = !isFormVisible"
      >
        {{ isFormVisible ? hideLabel : showLabel }}
      </button>
      
      <div v-show="isFormVisible" class="collapsible-form-content">
        <slot name="form"></slot>
      </div>
    </div>

    <!-- Always Visible Content -->
    <div class="section-content">
      <slot name="content"></slot>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface Props {
  title: string
  showToggle?: boolean
  showLabel?: string
  hideLabel?: string
  defaultExpanded?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showToggle: false,
  showLabel: 'Show Form',
  hideLabel: 'Hide Form',
  defaultExpanded: false
})

const isFormVisible = ref(props.defaultExpanded)
</script>

<style scoped>
.collapsible-section {
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 2px solid var(--color-border);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.section-header h3 {
  margin: 0;
  color: var(--color-text);
  font-size: 1.5rem;
}

.section-summary {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: var(--color-surface);
  border-radius: var(--radius-sm);
  border-left: 4px solid var(--color-primary);
}

.collapsible-form-area {
  margin-bottom: 1.5rem;
}

.toggle-button {
  padding: 0.5rem 1rem;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 500;
  transition: background-color 0.2s;
  width: auto;
  display: inline-block;
  margin-bottom: 1rem;
}

.toggle-button:hover {
  background: var(--color-primary-hover);
}

.collapsible-form-content {
  background: var(--color-surface);
  padding: 1.5rem;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
}
</style>
