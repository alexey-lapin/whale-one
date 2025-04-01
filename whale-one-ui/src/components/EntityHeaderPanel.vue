<script setup lang="ts">
import Panel from 'primevue/panel'

import EntityHeader from '@/components/EntityHeader.vue'

import type { EntityHeaderModel } from '@/model/BaseModel.ts'

defineProps<{
  header: string
  model: EntityHeaderModel
}>()
</script>

<template>
  <Panel
    :header="header"
    toggleable
    collapsed
  >
    <EntityHeader :model="model" />
  </Panel>
</template>
