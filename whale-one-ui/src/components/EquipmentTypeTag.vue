<script setup lang="ts">
const { active = true } = defineProps<{
  name: string
  active?: boolean
}>()
</script>

<template>
  <span
    class="font-mono rounded-md px-2"
    :class="{ active: active, inactive: !active }"
    >{{ name }}</span
  >
</template>

<style scoped>
.inactive {
  background-color: var(--p-content-hover-background);
}

.active {
  background-color: var(--p-highlight-background);
}
</style>
