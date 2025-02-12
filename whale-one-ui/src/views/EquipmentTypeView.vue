<script setup lang="ts">
import dayjs from 'dayjs'
import InputNumber from 'primevue/inputnumber'
import FloatLabel from 'primevue/floatlabel'
import Textarea from 'primevue/textarea'
import Fluid from 'primevue/fluid'
import Panel from 'primevue/panel'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'

import { errorToast, successToast } from '@/utils/toasts'
import { useToast } from 'primevue/usetoast'
import { onMounted, type Ref, ref } from 'vue'
import type { EquipmentTypeModel } from '@/model/EquipmentTypeModel.ts'
import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'
import EquipmentTypeAttribute from '@/components/EquipmentTypeAttribute.vue'

const toast = useToast()

const props = defineProps<{
  id: number
}>()

const model: Ref<EquipmentTypeModel> = ref({
  id: 0,
  version: 0,
  createdAt: "",
  createdBy: "",
  name: "",
  equipmentTypeId: -1,
})

const attributes: Ref<EquipmentTypeAttributeModel[]> = ref([])
const newAttribute: Ref<EquipmentTypeAttributeModel> = ref({
  id: 0,
  equipmentTypeId: props.id,
  version: 0,
  name: "",
  description: "",
  type: "text",
  metadata: undefined,
})

const loading = ref(false)
const addingNewAttribute = ref(false)

const getEquipmentType = () => {
  return fetch(`/api/equipment/types/${props.id}`)
    .then(response => response.json())
    .then(data => model.value = data)
    .catch(error => console.error(error))
}

const getAttributes = () => {
  return fetch(`/api/equipment/types/${props.id}/attributes`)
    .then(response => response.json())
    .then(data => attributes.value = data)
    .catch(error => console.error(error))
}

const updateEquipmentType = () => {
  loading.value = true
  fetch(`/api/equipment/types/${props.id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(model.value)
  })
    .then(response => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error('Failed to update project')
      }
    })
    .then(data => {
      model.value = data
      toast.add(successToast(`Project #${data.id} ${data.name} updated`))
    })
    .catch(error => {
      toast.add(errorToast(error.message))
      console.error(error)
    })
    .finally(() => {
      loading.value = false
    })
}

const onAttributeUpdated = () => {
  getAttributes()
  addingNewAttribute.value = false
}

const onAttributeDeleted = () => {
  addingNewAttribute.value = false
}

onMounted(() => {
  getEquipmentType()
  getAttributes()
})
</script>

<template>
  <div class="mt-5">
    <h1 class="text-xl">Equipment Type</h1>
    <div class="flex flex-col gap-5 my-4">
      <Panel header="Id" toggleable collapsed>
        <Fluid>
          <div class="mt-1 grid grid-cols-6 gap-3 xs:grid-cols-1">
            <FloatLabel variant="on">
              <InputNumber id="id" v-model="model.id" disabled />
              <label for="id">Id</label>
            </FloatLabel>
            <FloatLabel variant="on">
              <InputNumber id="version" v-model="model.version" disabled />
              <label for="version">Version</label>
            </FloatLabel>
            <FloatLabel variant="on" class="col-span-2">
              <InputText id="version"
                         :model-value="dayjs(model.createdAt).format('YYYY-MM-DD HH:mm:ss ZZ')"
                         disabled />
              <label for="version">Created At</label>
            </FloatLabel>
            <FloatLabel variant="on" class="col-span-2">
              <InputText id="version" v-model="model.createdBy" disabled />
              <label for="version">Created By</label>
            </FloatLabel>
          </div>
        </Fluid>
      </Panel>

      <Panel header="Info" toggleable>
        <div class="mt-1 flex flex-col gap-3">
          <FloatLabel variant="on" class="w-full">
            <InputText id="name" class="w-full" v-model="model.name" />
            <label for="name">Name</label>
          </FloatLabel>

<!--          <FloatLabel variant="on" class="w-full">-->
<!--            <Textarea class="w-full" v-model="project.description" />-->
<!--            <label for="1name">Description</label>-->
<!--          </FloatLabel>-->
        </div>
        <Button label="Save" icon="pi pi-save" class="mt-4" :loading="loading"
                @click="updateEquipmentType()" />
      </Panel>

      <Panel header="Attributes" toggleable>
        <div class="mt-1 flex flex-col gap-3">
          <EquipmentTypeAttribute v-for="(attribute, index) in attributes"
                       :key="attribute.id"
                       :modelValue="attribute"
                       @attribute-deleted="getAttributes()" />


          <EquipmentTypeAttribute v-if="addingNewAttribute"
                       v-model="newAttribute"
                       :editable="true"
                       @attribute-updated="onAttributeUpdated()"
                       @attribute-deleted="onAttributeDeleted()" />
        </div>
        <Button v-if="!addingNewAttribute"
                class="mt-3"
                label="New"
                severity="secondary"
                icon="pi pi-plus"
                @click="addingNewAttribute = true" />
      </Panel>
    </div>
  </div>
</template>
