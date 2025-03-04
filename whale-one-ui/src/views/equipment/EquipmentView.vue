<script setup lang="ts">
import { onMounted, ref, type Ref } from 'vue'

import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import FloatLabel from 'primevue/floatlabel'
import Panel from 'primevue/panel'
import ToggleButton from 'primevue/togglebutton'

import EntityHeader from '@/components/EntityHeader.vue'
import EquipmentAttribute from '@/components/EquipmentAttribute.vue'
import EquipmentTypeTag from '@/components/EquipmentTypeTag.vue'
import { invokeEquipmentGet, invokeEquipmentUpdate } from '@/client/equipmentClient.ts'
import { invokeAttributeListGet } from '@/client/equipmentTypeAttributeClient.ts'

import type { EquipmentAttributeModel, EquipmentModel } from '@/model/EquipmentModel.ts'
import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'

const props = defineProps<{
  id: number
}>()

const model: Ref<EquipmentModel> = ref({
  id: 0,
  version: 0,
  createdAt: '',
  createdBy: {
    id: 0,
    name: '',
  },
  active: false,
  name: '',
  typeId: -1,
  type: {
    id: 0,
    name: '',
  },
  attributes: [],
})

const equipmentTypeAttributes: Ref<EquipmentTypeAttributeModel[]> = ref([])
const attributes: Ref<EquipmentAttributeModel[]> = ref([])

const loading = ref(false)
const editing = ref(false)

const getEquipment = () => {
  return invokeEquipmentGet(props.id)
    .then((data) => (model.value = data))
    .catch(() => {})
}

const getEquipmentTypeAttributes = () => {
  invokeAttributeListGet('equipment', model.value.type.id)
    .then((data) => (equipmentTypeAttributes.value = data))
    .then(() => (attributes.value = collectAttributes()))
    .catch(() => {})
}

function collectAttributes(): EquipmentAttributeModel[] {
  return equipmentTypeAttributes.value.map((attribute) => ({
    id: 0,
    equipmentTypeAttributeId: attribute.id,
    value:
      model.value.attributes.find((a) => a.equipmentTypeAttributeId === attribute.id)?.value ??
      null,
  }))
}

const updateEquipment = () => {
  loading.value = true
  model.value.attributes = attributes.value.filter((a) => a.value)
  invokeEquipmentUpdate(model.value)
    .then((data) => {
      model.value = data
      model.value.attributes = collectAttributes()
      editing.value = false
    })
    .catch(() => {})
    .finally(() => (loading.value = false))
}

onMounted(() => {
  getEquipment().then(() => getEquipmentTypeAttributes())
})
</script>

<template>
  <div class="mt-5">
    <div class="flex flex-col gap-5 my-4">
      <EntityHeader
        :header="`Equipment #${model.id}`"
        :model="model"
      />

      <Panel header="Info">
        <template #header>
          <EquipmentTypeTag :name="model.type.name" />
        </template>
        <template #icons>
          <div class="flex gap-2">
            <ToggleButton
              v-model="model.active"
              off-label="Inactive"
              on-label="Active"
              size="small"
            />
            <Button
              severity="secondary"
              size="small"
              icon="pi pi-pencil"
              @click="editing = !editing"
            />
          </div>
        </template>
        <template #default>
          <div class="mt-1 flex flex-col gap-4">
            <FloatLabel
              variant="on"
              class="w-full"
            >
              <InputText
                id="name"
                class="w-full"
                v-model="model.name"
                :disabled="!editing"
              />
              <label for="name">Name</label>
            </FloatLabel>

            <!--          <FloatLabel variant="on" class="w-full">-->
            <!--            <Textarea class="w-full" v-model="project.description" />-->
            <!--            <label for="1name">Description</label>-->
            <!--          </FloatLabel>-->

            <template v-if="attributes.length > 0">
              <template v-for="(attribute, index) in equipmentTypeAttributes">
                <EquipmentAttribute
                  v-model="attributes[index].value"
                  :equipment-type-attribute="attribute"
                  :editing="editing"
                />
              </template>
            </template>
          </div>
          <Button
            v-if="editing"
            label="Save"
            icon="pi pi-save"
            class="mt-4"
            :loading="loading"
            @click="updateEquipment()"
          />
        </template>
      </Panel>
    </div>
  </div>
</template>

<!--<style scoped>-->
<!--:deep()-->
<!--</style>-->
