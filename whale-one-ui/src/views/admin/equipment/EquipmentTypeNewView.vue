<script setup lang="ts">
import { type Ref, ref } from 'vue'

import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import InputText from 'primevue/inputtext'
import Panel from 'primevue/panel'
import Textarea from 'primevue/textarea'
import { useToast } from 'primevue/usetoast'

import router from '@/router'
import EquipmentTypeParts from '@/components/EquipmentTypeParts.vue'
import { invokeEquipmentTypeCreate } from '@/client/equipmentTypeClient.ts'

import type { EquipmentTypeNewModel } from '@/model/EquipmentTypeModel.ts'
import type { BaseRefModel } from '@/model/BaseModel.ts'
import { errorToast } from '@/utils/toasts.ts'

const toast = useToast()

const model: Ref<EquipmentTypeNewModel> = ref({
  name: '',
  description: null,
  isAssembly: false,
  isDeployable: false,
})

const partEquipmentTypes: Ref<BaseRefModel[]> = ref([])

const loading = ref(false)

const create = () => {
  if (model.value.isAssembly) {
    if (partEquipmentTypes.value.length < 2) {
      toast.add(errorToast("At least 2 parts are required for assembly"))
      return
    }
    model.value.metadata = {}
    model.value.metadata.assemblyParts = partEquipmentTypes.value.map((part) => ({
      id: part.id,
      name: part.name,
    }))
  }
  loading.value = true
  invokeEquipmentTypeCreate(model.value)
    .then((data) => {
      return router.push(`/administration/equipment/types/${data.id}`)
    })
    .catch(() => {})
    .finally(() => {
      loading.value = false
    })
}
</script>

<template>
  <Fluid>
    <div class="flex flex-col gap-3 my-4">
      <h1 class="text-xl">Create New Equipment Type</h1>

      <div class="flex items-center gap-2">
        <Checkbox
          v-model="model.isAssembly"
          input-id="assembly"
          binary
        />
        <label for="assembly">Assembly</label>
      </div>

      <div class="flex items-center gap-2">
        <Checkbox
          v-model="model.isDeployable"
          input-id="deployable"
          binary
        />
        <label for="deployable">Deployable</label>
      </div>

      <FloatLabel variant="on">
        <InputText
          id="name"
          v-model="model.name"
        />
        <label for="name">Name</label>
      </FloatLabel>

      <FloatLabel variant="on">
        <Textarea v-model="model.description" />
        <label for="1name">Description</label>
      </FloatLabel>

      <Panel
        v-if="model.isAssembly"
        header="Assembly Parts"
      >
        <EquipmentTypeParts v-model="partEquipmentTypes" />
      </Panel>
    </div>
  </Fluid>

  <Button
    label="Create"
    icon="pi pi-plus"
    :loading="loading"
    @click="create()"
  />
</template>
