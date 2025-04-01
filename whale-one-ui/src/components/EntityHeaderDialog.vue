<script setup lang="ts">
import { ref } from 'vue'
import Dialog from 'primevue/dialog'

import EntityHeader from '@/components/EntityHeader.vue'

import type { EntityHeaderModel } from '@/model/BaseModel.ts'

defineProps<{
  model: EntityHeaderModel
}>()

const visible = ref(false)

const toggle = () => {
  visible.value = !visible.value
}
</script>

<template>
  <slot :toggle="toggle" />
  <Dialog
    v-model:visible="visible"
    modal
    dismissable-mask
    :closable="false"
  >
    <EntityHeader :model="model" />
  </Dialog>
</template>
