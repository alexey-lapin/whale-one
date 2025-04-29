<script setup lang="ts">
import Button from 'primevue/button'

import DeploymentEquipmentNode from '@/components/DeploymentEquipmentNode.vue'

import type { DeploymentEquipmentElementModel } from '@/model/DeploymentModel.ts'
import { invokeDeploymentEquipmentDelete } from '@/client/deploymentClient.ts'

const props = defineProps<{
  deploymentId: number
  deploymentEquipment: DeploymentEquipmentElementModel
}>()

const emits = defineEmits(['equipment-deleted'])

const deleteEquipment = () => {
  invokeDeploymentEquipmentDelete(
    props.deploymentId,
    props.deploymentEquipment.id,
  ).then(() => {
    emits('equipment-deleted')
  })
}
</script>

<template>
  <div class="flex justify-between items-stretch border hover:border-surface-500 dark:border-surface-700 rounded p-2">
    <DeploymentEquipmentNode class="m-2" :deployment-equipment="deploymentEquipment" />
    <Button
      class="hover:!text-red-500"
      icon="pi pi-trash"
      size="small"
      severity="secondary"
      variant="text"
      @click="deleteEquipment()"
    />
  </div>
</template>
