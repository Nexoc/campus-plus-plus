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
  padding: 1.5rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: var(--color-surface);
  box-shadow: var(--shadow-sm);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid var(--color-border);
}

.section-header h3 {
  margin: 0;
  color: var(--color-text);
  font-size: 1.25rem;
  font-weight: 600;
}

.section-summary {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: var(--color-primary-light);
  border-radius: var(--radius-sm);
  border-left: 4px solid var(--color-primary);
  color: var(--color-text);
}

.collapsible-form-area {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
}

.toggle-button {
  padding: 0.625rem 1rem;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 600;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  width: auto;
  display: inline-block;
  margin-bottom: 1rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.toggle-button:hover {
  background: var(--color-primary-hover);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  transform: translateY(-1px);
}

.toggle-button:active {
  transform: translateY(0);
}

.collapsible-form-content {
  background: var(--color-surface);
  padding: 1.5rem;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  margin-top: 1rem;
}

.section-content {
  margin-top: 1rem;
}
</style>
