<script setup lang="ts">
import Message from 'primevue/message'

const model = defineModel<boolean>({ required: true })
</script>

<template>
  <Message
    v-if="model"
    severity="warn"
  >
    <p>
      The data is stale. Please refresh the page to retrieve the latest information. Any unsaved
      changes will be lost.
    </p>
  </Message>
</template>
