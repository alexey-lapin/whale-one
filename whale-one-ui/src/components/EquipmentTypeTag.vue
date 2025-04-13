<script setup lang="ts">
defineProps<{
  name: string
}>()
</script>

<template>
  <span class="font-mono lowercase rounded-md px-2">{{ name }}</span>
</template>

<style scoped>
  span {
    background-color: var(--p-highlight-background);
  }
</style>
