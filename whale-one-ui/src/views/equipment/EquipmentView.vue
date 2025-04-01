<script setup lang="ts">
import { computed, onMounted, ref, type Ref } from 'vue'

import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import Panel from 'primevue/panel'
import Select from 'primevue/select'
import ToggleButton from 'primevue/togglebutton'
import EquipmentAttribute from '@/components/EquipmentAttribute.vue'
import EquipmentTypeTag from '@/components/EquipmentTypeTag.vue'
import { invokeEquipmentGet, invokeEquipmentUpdate } from '@/client/equipmentClient.ts'
import { invokeAttributeListGet } from '@/client/equipmentTypeAttributeClient.ts'

import type { EquipmentAttributeModel, EquipmentModel } from '@/model/EquipmentModel.ts'
import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'
import type {
  EquipmentTypeManufacturerModel,
  EquipmentTypeModel,
} from '@/model/EquipmentTypeModel.ts'
import { invokeEquipmentTypeGet } from '@/client/equipmentTypeClient.ts'
import EntityHeaderDialog from '@/components/EntityHeaderDialog.vue'

const props = defineProps<{
  id: number
}>()

const model: Ref<EquipmentModel | null> = ref(null)

const equipmentType: Ref<EquipmentTypeModel | null> = ref(null)
const equipmentTypeAttributes: Ref<EquipmentTypeAttributeModel[]> = ref([])
const attributes: Ref<EquipmentAttributeModel[]> = ref([])

const loading = ref(false)
const editing = ref(false)

const getEquipment = () => {
  return invokeEquipmentGet(props.id)
    .then((data) => (model.value = data))
    .catch(() => {})
}

const getEquipmentType = () => {
  if (!model.value) {
    return
  }
  invokeEquipmentTypeGet(model.value.type.id)
    .then((data) => (equipmentType.value = data))
    .catch(() => {})
}

const getEquipmentTypeAttributes = () => {
  if (!model.value) {
    return
  }
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
      model.value?.attributes.find((a) => a.equipmentTypeAttributeId === attribute.id)?.value ??
      null,
  }))
}

const updateEquipment = () => {
  if (!model.value) {
    return
  }
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

const manufacturers = computed(() => {
  return (
    (equipmentType.value?.metadata?.manufacturers as EquipmentTypeManufacturerModel[]) || []
  ).map((m) => m.name)
})

const models = computed(() => {
  return (
    ((equipmentType.value?.metadata?.manufacturers as EquipmentTypeManufacturerModel[]) || []).find(
      (m) => m.name === model.value?.manufacturer,
    )?.models || []
  )
})

const onManufacturerChange = (newValue: string | null) => {
  if (!model.value) {
    return
  }
  model.value.model = ''
}

onMounted(() => {
  getEquipment().then(() => {
    getEquipmentType()
    getEquipmentTypeAttributes()
  })
})
</script>

<template>
  <div
    v-if="model"
    class="my-4 flex flex-col gap-4"
  >
    <Panel>
      <template #header>
        <EntityHeaderDialog
          :model="model"
          v-slot="{ toggle }"
        >
          <EquipmentTypeTag
            :name="model.type.name"
            class="cursor-pointer"
            @click="toggle()"
          />
        </EntityHeaderDialog>
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
            variant="text"
            size="small"
            severity="secondary"
            icon="pi pi-pencil"
            @click="editing = !editing"
          />
        </div>
      </template>
      <template #default>
        <Fluid>
          <div class="mt-1 flex flex-col gap-4">
            <FloatLabel variant="on">
              <InputText
                id="name"
                v-model="model.name"
                :disabled="!editing"
              />
              <label for="name">Name</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <Select
                v-model="model.manufacturer"
                :options="manufacturers"
                :disabled="!editing"
                @change="onManufacturerChange($event.value)"
              />
              <label>Manufacturer</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <Select
                v-model="model.model"
                :options="models"
                :disabled="!editing"
              />
              <label>Model</label>
            </FloatLabel>

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
        </Fluid>
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
</template>
