<script setup lang="ts">
import { onMounted, type Ref, ref, watch } from 'vue'

import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Tag from 'primevue/tag'

import router from '@/router'
import { invokeEquipmentTypeListGet } from '@/client/equipmentTypeClient.ts'
import { invokeEquipmentCreate } from '@/client/equipmentClient.ts'
import { type EquipmentNewModel, EquipmentStatus } from '@/model/EquipmentModel.ts'
import type {
  EquipmentTypeManufacturerModel,
  EquipmentTypeModel,
} from '@/model/EquipmentTypeModel.ts'
import EquipmentItemDropdown from '@/components/EquipmentItemDropdown.vue'

const model: Ref<EquipmentNewModel> = ref({
  name: null,
  type: null,
  status: null,
  assemblyScope: 'PERSISTENT',
})

const equipmentTypes: Ref<EquipmentTypeModel[]> = ref([])
const equipmentType: Ref<EquipmentTypeModel | null> = ref(null)
const manufacturers: Ref<EquipmentTypeManufacturerModel[]> = ref([])
const manufacturer: Ref<EquipmentTypeManufacturerModel | null> = ref(null)

const loading = ref(false)

const create = () => {
  loading.value = true
  invokeEquipmentCreate(model.value)
    .then((data) => router.push(`/equipment/${data.id}`))
    .catch(() => {})
    .finally(() => (loading.value = false))
}

watch(
  () => equipmentType.value,
  async (newValue, oldValue) => {
    let reset = false
    if (!newValue) {
      model.value.type = null
      manufacturers.value = []
      reset = true
    } else if (newValue.id !== oldValue?.id) {
      model.value.type = {
        id: newValue.id,
        name: newValue.name,
      }
      if (newValue.isAssembly && newValue.metadata?.assemblyParts) {
        model.value.assemblyParts = newValue.metadata.assemblyParts.map((p) => {
          return {
            typeId: p.id,
            equipmentId: -1,
          }
        })
      } else {
        model.value.assemblyParts = null
      }
      manufacturers.value =
        (newValue.metadata?.manufacturers as EquipmentTypeManufacturerModel[]) || []
      reset = true
    }
    if (reset) {
      model.value.manufacturer = null
      model.value.model = null
      manufacturer.value = null
    }
  },
)

const onManufacturerChange = (newValue: EquipmentTypeManufacturerModel | null) => {
  if (newValue) {
    model.value.manufacturer = newValue.name
  } else {
    model.value.manufacturer = null
    model.value.model = null
  }
}

onMounted(() => {
  invokeEquipmentTypeListGet(0, 50, {})
    .then((data) => (equipmentTypes.value = data.items))
    .catch(() => {})
})
</script>

<template>
  <div class="mt-5">
    <h1 class="text-xl">Create New Equipment</h1>
    <Fluid>
      <div class="flex flex-col gap-3 my-3">
        <FloatLabel variant="on">
          <Select
            v-model="equipmentType"
            :options="equipmentTypes"
            option-label="name"
          >
            <template #option="{ option }">
              <div class="flex items-center gap-2">
                <span>{{ option.name }}</span>
                <Tag
                  v-if="option.isAssembly"
                  severity="primary"
                  icon="pi pi-cog"
                >
                  assembly
                </Tag>
              </div>
            </template>
          </Select>
          <label>Type</label>
        </FloatLabel>

        <FloatLabel variant="on">
          <InputText
            id="name"
            v-model="model.name"
          />
          <label>Name</label>
        </FloatLabel>

        <FloatLabel variant="on">
          <Select
            id="status"
            v-model="model.status"
            :options="Object.values(EquipmentStatus)"
          />
          <label>Status</label>
        </FloatLabel>

        <FloatLabel
          v-if="equipmentType?.isAssembly === false"
          variant="on"
        >
          <Select
            v-model="manufacturer"
            :options="manufacturers"
            option-label="name"
            @change="onManufacturerChange($event.value)"
          />
          <label>Manufacturer</label>
        </FloatLabel>

        <FloatLabel
          v-if="equipmentType?.isAssembly === false"
          variant="on"
        >
          <Select
            v-model="model.model"
            :options="manufacturer?.models"
          />
          <label>Model</label>
        </FloatLabel>

        <div
          v-if="equipmentType?.isAssembly"
          class="flex flex-col gap-3"
        >
          <template
            v-if="model.assemblyParts"
            v-for="(part, index) in equipmentType?.metadata?.assemblyParts"
          >
            <FloatLabel variant="on">
              <EquipmentItemDropdown
                v-model="model.assemblyParts[index]"
                :type="part"
              />
              <label>{{ part.name }}</label>
            </FloatLabel>
          </template>
        </div>
      </div>
    </Fluid>
    <Button
      label="Create"
      icon="pi pi-plus"
      :loading="loading"
      @click="create()"
    />
  </div>
</template>
