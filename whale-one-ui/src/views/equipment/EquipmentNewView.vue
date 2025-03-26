<script setup lang="ts">
import { type Ref, ref, watch } from 'vue'

import AutoComplete from 'primevue/autocomplete'
import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'

import router from '@/router'
import {
  invokeEquipmentTypeGet,
  invokeEquipmentTypeItemListGet,
} from '@/client/equipmentTypeClient.ts'
import { invokeEquipmentCreate } from '@/client/equipmentClient.ts'

import type { BaseRefModel } from '@/model/BaseModel.ts'
import type { EquipmentNewModel } from '@/model/EquipmentModel.ts'
import type {
  EquipmentTypeManufacturerModel,
  EquipmentTypeModel,
} from '@/model/EquipmentTypeModel.ts'

const model: Ref<EquipmentNewModel> = ref({
  name: null,
  type: null,
})

const equipmentTypes: Ref<BaseRefModel[]> = ref([])
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

const equipmentTypeItems = (q: string | null) => {
  invokeEquipmentTypeItemListGet(q)
    .then((data) => (equipmentTypes.value = data))
    .catch(() => {})
}

watch(
  () => model.value.type,
  async (newValue, oldValue) => {
    let reset = false
    if (!newValue) {
      equipmentType.value = null
      manufacturers.value = []
      reset = true
    } else if (newValue.id !== oldValue?.id) {
      await invokeEquipmentTypeGet(newValue.id).then((data) => {
        equipmentType.value = data
        manufacturers.value =
          (data.metadata?.manufacturers as EquipmentTypeManufacturerModel[]) || []
      })
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
</script>

<template>
  <div class="mt-5">
    <h1 class="text-xl">Create New Equipment</h1>
    <Fluid>
      <div class="flex flex-col gap-3 my-3">
        <FloatLabel variant="on">
          <AutoComplete
            v-model="model.type"
            dropdown
            :suggestions="equipmentTypes"
            option-label="name"
            force-selection
            @complete="equipmentTypeItems($event.query)"
          />
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
            v-model="manufacturer"
            :options="manufacturers"
            option-label="name"
            @change="onManufacturerChange($event.value)"
          />
          <label>Manufacturer</label>
        </FloatLabel>

        <FloatLabel variant="on">
          <Select
            v-model="model.model"
            :options="manufacturer?.models"
          />
          <label>Model</label>
        </FloatLabel>
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
