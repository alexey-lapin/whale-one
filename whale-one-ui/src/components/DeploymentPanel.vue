<script setup lang="ts">
const {start = true, end = true} = defineProps<{
  start?: boolean
  end?: boolean
  icon: string
  iconClasses: string
}>()
</script>

<template>
  <div class="p-timeline-event">
    <div class="p-timeline-event-separator">
      <div v-if="start" class="h-5 p-timeline-event-connector p-timeline-event-connector-start"></div>
      <span
        :class="`flex w-8 h-8 items-center justify-center rounded-full z-10 shadow-sm ${iconClasses}`"
      >
        <i :class="icon" />
      </span>
      <div v-if="end" class="p-timeline-event-connector"></div>
    </div>
    <div class="p-timeline-event-content">
      <slot />
    </div>
  </div>
</template>

<style scoped>
:deep(.p-timeline-event-connector.p-timeline-event-connector-start) {
  flex-grow: 0;
}

:deep(.p-timeline-event-content) {
  padding: 0 0 0 1rem !important;
}
</style>
