<template>
  <table class="entity-table">
    <thead>
      <tr>
        <th v-for="col in columns" :key="col.key" :class="col.thClass" @click="col.sortable ? $emit('sort', col.key) : null">
          {{ col.label }}
          <span v-if="col.sortable && sortBy === col.key">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
        </th>
        <th v-if="hasActions" class="actions-col">Actions</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="row in rows" :key="row[rowKey]" class="entity-row">
        <td v-for="col in columns" :key="col.key">
          <slot :name="col.key" :row="row">{{ row[col.key] }}</slot>
        </td>
        <td v-if="hasActions" class="entity-actions actions-col">
          <slot name="actions" :row="row" />
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
const props = defineProps<{
  columns: Array<{ key: string, label: string, thClass?: string, sortable?: boolean }>
  rows: Array<Record<string, any>>
  rowKey: string
  hasActions?: boolean
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}>()
const emit = defineEmits(['sort'])
</script>
