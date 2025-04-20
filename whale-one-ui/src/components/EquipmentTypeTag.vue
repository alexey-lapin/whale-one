<script setup lang="ts">
const { active = true } = defineProps<{
  name: string
  active?: boolean
}>()
</script>

<template>
  <span class="font-mono rounded-md px-2" :class="{active: active}">{{ name }}</span>
</template>

<style scoped>
.active {
  background-color: var(--p-highlight-background);
}
</style>
