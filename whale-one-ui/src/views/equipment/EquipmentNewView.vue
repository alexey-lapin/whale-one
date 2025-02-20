<script setup lang="ts">
import FloatLabel from 'primevue/floatlabel'
import Textarea from 'primevue/textarea'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import { useToast } from 'primevue/usetoast'
import { type Ref, ref } from 'vue'
import { errorToast, successToast } from '@/utils/toasts.ts'
import router from '@/router'
import AutoComplete from 'primevue/autocomplete'
import type { EquipmentTypeItemModel, EquipmentTypeModel } from '@/model/EquipmentTypeModel.ts'
import type { EquipmentModel, EquipmentNewModel } from '@/model/EquipmentModel.ts'

import type { BaseRefModel } from '@/model/BaseModel.ts'

const toast = useToast()

const model: Ref<EquipmentNewModel> = ref({
  name: null,
  type: null
})

const equipmentTypes: Ref<BaseRefModel[]> = ref([])

const loading = ref(false)

const create = () => {
  loading.value = true
  fetch('/api/equipment', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(model.value)
  })
    .then(response => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error('Failed to create equipment')
      }
    })
    .then(data => {
      toast.add(successToast(`Equipment #${data.id} ${data.name} created`))
      return router.push(`/equipment/${data.id}`)
    })
    .catch(error => {
      toast.add(errorToast(error.message))
      console.error(error)
    })
    .finally(() => {
      loading.value = false
    })
}

const equipmentTypeItems = (q: string | null) => {
  fetch(`/api/equipment/types/items?q=${q ?? ''}`)
    .then(response => response.json())
    .then(data => equipmentTypes.value = data)
    .catch(error => console.error(error))
}

// const onEquipmentTypeSelected = (event: EquipmentTypeItemModel | string | null) => {
//   if (event === null) {
//     model.value.type = -1
//     return
//   } else if (typeof event === 'string') {
//     return
//   }
//   model.value.type = event.id
// }
</script>

<template>
  <div class="mt-5">
    <h1 class="text-xl">Create New Equipment</h1>
    <div class="flex flex-col gap-5 my-4">
      <FloatLabel variant="on" class="w-full">
        <AutoComplete v-model="model.type" dropdown :suggestions="equipmentTypes"
                      option-label="name"
                      force-selection
                      @complete="equipmentTypeItems($event.query)" />
        <label for="1name">Type</label>
      </FloatLabel>

      <FloatLabel variant="on" class="w-full">
        <InputText id="name" class="w-full" v-model="model.name" />
        <label for="name">Name</label>
      </FloatLabel>

<!--      <FloatLabel variant="on" class="w-full">-->
<!--        <Textarea class="w-full" v-model="model.description" placeholder1="Description" />-->
<!--        <label for="1name">Description</label>-->
<!--      </FloatLabel>-->
    </div>
    <Button label="Create" icon="pi pi-plus" :loading="loading" @click="create()"></Button>
  </div>
</template>
