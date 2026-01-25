<script setup lang="ts">
defineProps<{
  icon: string
  label: string
}>()
</script>

<template>
  <div class="flex items-center gap-2">
    <span
      :class="`flex w-8 h-8 items-center justify-center rounded-full z-10 shadow-xs`"
    >
      <i :class="`pi ${icon}`" />
    </span>
    <span>{{ label}}</span>
  </div>
</template>
