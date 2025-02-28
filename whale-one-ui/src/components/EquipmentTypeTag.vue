<script setup lang="ts">
defineProps<{
  name: string
}>()
</script>

<template>
  <span class="font-mono lowercase rounded-md px-2 bg-cyan-50">{{ name }}</span>
</template>
