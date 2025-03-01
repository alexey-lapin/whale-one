<script setup lang="ts">
import Button from 'primevue/button'
import Panel from 'primevue/panel'

import type { DeploymentEquipmentItemModel } from '@/model/DeploymentModel.ts'
import { invokeDeploymentEquipmentDelete } from '@/client/deploymentClient.ts'
import EquipmentTypeTag from '@/components/EquipmentTypeTag.vue'
import { invokeAttributeListGet } from '@/client/equipmentTypeAttributeClient.ts'
import { onMounted, type Ref, ref } from 'vue'
import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'
import EquipmentAttribute from '@/components/EquipmentAttribute.vue'
import type { EquipmentAttributeModel } from '@/model/EquipmentModel.ts'

const props = defineProps<{
  deploymentEquipment: DeploymentEquipmentItemModel
}>()

const emits = defineEmits(['equipment-deleted'])

const equipmentTypeAttributes: Ref<EquipmentTypeAttributeModel[]> = ref([])
const attributes: Ref<EquipmentAttributeModel[]> = ref([])

const loading = ref(false)
const editing = ref(false)

const getDeploymentAttributes = () => {
  invokeAttributeListGet('deployment', props.deploymentEquipment.equipmentTypeRef.id)
    .then((data) => (equipmentTypeAttributes.value = data))
    .then(() => (attributes.value = collectAttributes()))
    .catch(() => {})
}

function collectAttributes(): EquipmentAttributeModel[] {
  return equipmentTypeAttributes.value.map((attribute) => ({
    id: 0,
    equipmentTypeAttributeId: attribute.id,
    value: null,
  }))
}

const deleteEquipment = () => {
  invokeDeploymentEquipmentDelete(
    props.deploymentEquipment.deploymentId,
    props.deploymentEquipment.equipmentRef.id,
  ).then(() => {
    emits('equipment-deleted')
  })
}

onMounted(() => {
  getDeploymentAttributes()
})
</script>

<template>
  <!--  <div class="flex items-center gap-2">-->
  <!--    <p>{{ deploymentEquipment.equipmentTypeRef.name }}</p>-->
  <!--    <p>{{ deploymentEquipment.equipmentRef.name }}</p>-->
  <!--    <Button-->
  <!--      icon="pi pi-trash"-->
  <!--      variant="text"-->
  <!--      @click="deleteEquipment()"-->
  <!--    />-->
  <!--  </div>-->
  <Panel>
    <template #header>
      <div class="flex flex-wrap gap-2 items-center">
        <!--        <span class="font-mono lowercase rounded-md px-2 bg-cyan-100">{{-->
        <!--          deploymentEquipment.equipmentTypeRef.name-->
        <!--        }}</span>-->
        <EquipmentTypeTag :name="deploymentEquipment.equipmentTypeRef.name" />
        <span class="font-bold">{{ deploymentEquipment.equipmentRef.name }}</span>
      </div>
    </template>
    <template #icons>
      <Button
        icon="pi pi-trash"
        variant="text"
        severity="secondary"
        @click="deleteEquipment()"
      />
    </template>
    <template #default>
      <div class="mt-1 flex flex-col gap-4">
        <template v-if="attributes.length > 0">
          <template v-for="(attribute, index) in equipmentTypeAttributes">
            <EquipmentAttribute
              v-model="attributes[index].value"
              :equipment-type-attribute="attribute"
              :editing="false"
            />
          </template>
        </template>
      </div>
    </template>
  </Panel>
</template>
