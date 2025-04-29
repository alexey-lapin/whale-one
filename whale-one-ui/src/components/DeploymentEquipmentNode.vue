<script setup lang="ts">
import { computed } from 'vue'

import EquipmentTypeTag from '@/components/EquipmentTypeTag.vue'

import type { DeploymentEquipmentElementModel } from '@/model/DeploymentModel.ts'

const props = defineProps<{
  deploymentEquipment: DeploymentEquipmentElementModel
  level?: number
}>()

const currentLevel = computed(() => props.level ?? 0)
const hasChildren = computed(() => props.deploymentEquipment.assemblyParts?.length > 0)
</script>

<template>
  <div>
    <div class="flex items-center">
      <div class="flex items-center gap-2">
        <EquipmentTypeTag :name="deploymentEquipment.type.name" />
        <span class="font-bold">{{ deploymentEquipment.name }}</span>
      </div>
    </div>

    <div
      v-if="hasChildren"
      class="pl-2 ml-2 mt-1 flex flex-col gap-1"
    >
      <template
        v-for="part in deploymentEquipment.assemblyParts"
        :key="part.id"
      >
        <div class="flex gap-2">
          <span>+</span>
          <DeploymentEquipmentNode
            :deployment-equipment="part"
            :level="currentLevel + 1"
          />
        </div>
      </template>
    </div>
  </div>
</template>
