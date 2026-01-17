<template>
  <table class="entity-table">
    <thead>
      <tr>
        <th
          v-for="col in columns"
          :key="col.key"
          :class="col.thClass"
        >
          <div class="th-wrapper">
            <div class="th-click" @click="col.sortable ? $emit('sort', col.sortKey ?? col.key) : null">
              <slot :name="`th-${col.key}`" :col="col">
                {{ col.label }}
                <span v-if="col.sortable && sortBy === (col.sortKey ?? col.key)">{{ sortOrder === 'asc' ? '↑' : '↓' }}</span>
              </slot>
            </div>
          </div>
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
          <slot name="actions" :row="row"></slot>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
const props = defineProps<{
  columns: Array<{ key: string, label: string, thClass?: string, sortable?: boolean, sortKey?: string }>
  rows: Array<Record<string, any>>
  rowKey: string
  hasActions?: boolean
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}>()
const emit = defineEmits(['sort'])
</script>

<style scoped>
.th-wrapper {
  display: flex;
  align-items: flex-start;
}
.th-click {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
</style>
