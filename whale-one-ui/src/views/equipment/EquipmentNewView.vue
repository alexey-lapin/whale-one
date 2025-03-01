<script setup lang="ts">
import { type Ref, ref } from 'vue'

import AutoComplete from 'primevue/autocomplete'
import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import InputText from 'primevue/inputtext'

import router from '@/router'
import { invokeEquipmentTypeItemListGet } from '@/client/equipmentTypeClient.ts'
import { invokeEquipmentCreate } from '@/client/equipmentClient.ts'

import type { BaseRefModel } from '@/model/BaseModel.ts'
import type { EquipmentNewModel } from '@/model/EquipmentModel.ts'

const model: Ref<EquipmentNewModel> = ref({
  name: null,
  type: null,
})

const equipmentTypes: Ref<BaseRefModel[]> = ref([])

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
</script>

<template>
  <div class="mt-5">
    <h1 class="text-xl">Create New Equipment</h1>
    <div class="flex flex-col gap-5 my-4">
      <FloatLabel
        variant="on"
        class="w-full"
      >
        <AutoComplete
          v-model="model.type"
          dropdown
          :suggestions="equipmentTypes"
          option-label="name"
          force-selection
          @complete="equipmentTypeItems($event.query)"
        />
        <label for="1name">Type</label>
      </FloatLabel>

      <FloatLabel
        variant="on"
        class="w-full"
      >
        <InputText
          id="name"
          class="w-full"
          v-model="model.name"
        />
        <label for="name">Name</label>
      </FloatLabel>

      <!--      <FloatLabel variant="on" class="w-full">-->
      <!--        <Textarea class="w-full" v-model="model.description" placeholder1="Description" />-->
      <!--        <label for="1name">Description</label>-->
      <!--      </FloatLabel>-->
    </div>
    <Button
      label="Create"
      icon="pi pi-plus"
      :loading="loading"
      @click="create()"
    ></Button>
  </div>
</template>
