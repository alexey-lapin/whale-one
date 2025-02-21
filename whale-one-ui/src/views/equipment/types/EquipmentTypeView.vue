<script setup lang="ts">
import { onMounted, type Ref, ref } from 'vue'
import dayjs from 'dayjs'

import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import Fluid from 'primevue/fluid'
import Panel from 'primevue/panel'
import Textarea from 'primevue/textarea'
import { useToast } from 'primevue/usetoast'

import EquipmentTypeAttribute from '@/components/EquipmentTypeAttribute.vue'

import type { EquipmentTypeModel } from '@/model/EquipmentTypeModel.ts'
import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'
import {
  invokeEquipmentTypeAttributeListGet,
  invokeEquipmentTypeGet,
  invokeEquipmentTypeUpdate,
} from '@/client/equipmentTypeClient.ts'

const toast = useToast()

const props = defineProps<{
  id: number
}>()

const model: Ref<EquipmentTypeModel> = ref({
  id: 0,
  version: 0,
  createdAt: '',
  createdBy: {
    id: 0,
    name: '',
  },
  name: '',
})

const attributes: Ref<EquipmentTypeAttributeModel[]> = ref([])
const newAttribute: Ref<EquipmentTypeAttributeModel> = ref({
  id: 0,
  equipmentTypeId: props.id,
  version: 0,
  name: '',
  description: '',
  type: 'text',
  metadata: undefined,
})

const loading = ref(false)
const editing = ref(false)
const addingNewAttribute = ref(false)

const getEquipmentType = () => {
  invokeEquipmentTypeGet(props.id)
    .then((data) => (model.value = data))
    .catch(() => {})
}

const getAttributes = () => {
  invokeEquipmentTypeAttributeListGet(props.id)
    .then((data) => (attributes.value = data))
    .catch(() => {})
}

const updateEquipmentType = () => {
  loading.value = true
  invokeEquipmentTypeUpdate(model.value)
    .then((data) => {
      model.value = data
      editing.value = false
    })
    .catch(() => {})
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
      <Panel
        header="Id"
        toggleable
        collapsed
      >
        <Fluid>
          <div class="mt-1 grid grid-cols-6 gap-3 xs:grid-cols-1">
            <FloatLabel variant="on">
              <InputNumber
                id="id"
                v-model="model.id"
                disabled
              />
              <label for="id">Id</label>
            </FloatLabel>
            <FloatLabel variant="on">
              <InputNumber
                id="version"
                v-model="model.version"
                disabled
              />
              <label for="version">Version</label>
            </FloatLabel>
            <FloatLabel
              variant="on"
              class="col-span-2"
            >
              <InputText
                id="version"
                :model-value="dayjs(model.createdAt).format('YYYY-MM-DD HH:mm:ss ZZ')"
                disabled
              />
              <label for="version">Created At</label>
            </FloatLabel>
            <FloatLabel
              variant="on"
              class="col-span-2"
            >
              <InputText
                id="version"
                v-model="model.createdBy.name"
                disabled
              />
              <label for="version">Created By</label>
            </FloatLabel>
          </div>
        </Fluid>
      </Panel>

      <Panel header="Info">
        <template #icons>
          <div class="flex gap-2">
            <Button
              severity="secondary"
              size="small"
              icon="pi pi-pencil"
              @click="editing = !editing"
            />
          </div>
        </template>
        <template #default>
          <div class="mt-1 flex flex-col gap-3">
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

            <FloatLabel
              variant="on"
              class="w-full"
            >
              <Textarea
                class="w-full"
                :disabled="!editing"
              />
              <label for="1name">Description</label>
            </FloatLabel>
          </div>
          <Button
            v-if="editing"
            label="Save"
            icon="pi pi-save"
            class="mt-4"
            :loading="loading"
            @click="updateEquipmentType()"
          />
        </template>
      </Panel>

      <Panel
        header="Attributes"
        toggleable
      >
        <div class="mt-1 flex flex-col gap-3">
          <EquipmentTypeAttribute
            v-for="(attribute, index) in attributes"
            :key="attribute.id"
            :modelValue="attribute"
            @attribute-deleted="getAttributes()"
          />

          <EquipmentTypeAttribute
            v-if="addingNewAttribute"
            v-model="newAttribute"
            :editable="true"
            @attribute-updated="onAttributeUpdated()"
            @attribute-deleted="onAttributeDeleted()"
          />
        </div>
        <Button
          v-if="!addingNewAttribute"
          class="mt-3"
          label="New"
          severity="secondary"
          icon="pi pi-plus"
          @click="addingNewAttribute = true"
        />
      </Panel>
    </div>
  </div>
</template>
