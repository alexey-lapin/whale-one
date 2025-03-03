<script setup lang="ts">
import { onMounted, type Ref, ref } from 'vue'

import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import InputText from 'primevue/inputtext'
import Panel from 'primevue/panel'
import Textarea from 'primevue/textarea'

import EquipmentTypeAttribute from '@/components/EquipmentTypeAttribute.vue'

import type { EquipmentTypeModel } from '@/model/EquipmentTypeModel.ts'
import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'
import {
  invokeEquipmentTypeGet,
  invokeEquipmentTypeUpdate,
} from '@/client/equipmentTypeClient.ts'
import EntityHeader from '@/components/EntityHeader.vue'
import {
  invokeAttributeListGet
} from '@/client/equipmentTypeAttributeClient.ts'

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
const deploymentAttributes: Ref<EquipmentTypeAttributeModel[]> = ref([])

const newAttribute: Ref<EquipmentTypeAttributeModel> = ref({
  id: 0,
  equipmentTypeId: props.id,
  version: 0,
  name: '',
  order: 0,
  description: '',
  type: 'text',
  metadata: undefined,
})

const newDeploymentAttribute: Ref<EquipmentTypeAttributeModel> = ref({
  id: 0,
  equipmentTypeId: props.id,
  version: 0,
  name: '',
  order: 0,
  description: '',
  type: 'text',
  metadata: undefined,
})

const loading = ref(false)
const editing = ref(false)
const addingNewAttribute = ref(false)
const addingNewDeploymentAttribute = ref(false)

const getEquipmentType = () => {
  invokeEquipmentTypeGet(props.id)
    .then((data) => (model.value = data))
    .catch(() => {})
}

const getAttributes = () => {
  invokeAttributeListGet('equipment', props.id)
    .then((data) => (attributes.value = data))
    .catch(() => {})
}

const getDeploymentAttributes = () => {
  invokeAttributeListGet('deployment', props.id)
    .then((data) => (deploymentAttributes.value = data))
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

const onDeploymentAttributeUpdated = () => {
  getDeploymentAttributes()
  addingNewDeploymentAttribute.value = false
}

const onDeploymentAttributeDeleted = () => {
  addingNewDeploymentAttribute.value = false
}

onMounted(() => {
  getEquipmentType()
  getAttributes()
  getDeploymentAttributes()
})
</script>

<template>
  <div class="mt-5">
    <div class="flex flex-col gap-5 my-4">
      <EntityHeader
        :header="`Equipment Type #${model.id}`"
        :model="model"
      />

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
        header="Equipment Attributes"
        toggleable
      >
        <div class="mt-1 flex flex-col gap-3">
          <EquipmentTypeAttribute
            v-for="(attribute, index) in attributes"
            attribute-entity="equipment"
            :key="attribute.id"
            :modelValue="attribute"
            @attribute-deleted="getAttributes()"
          />

          <EquipmentTypeAttribute
            v-if="addingNewAttribute"
            v-model="newAttribute"
            attribute-entity="equipment"
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

      <Panel
        header="Deployment Attributes 1"
        toggleable
      >
        <div class="mt-1 flex flex-col gap-3">
          <EquipmentTypeAttribute
            v-for="(attribute, index) in deploymentAttributes"
            attribute-entity="deployment"
            :key="attribute.id"
            :modelValue="attribute"
            @attribute-deleted="getDeploymentAttributes()"
          />

          <EquipmentTypeAttribute
            v-if="addingNewDeploymentAttribute"
            v-model="newDeploymentAttribute"
            attribute-entity="deployment"
            :editable="true"
            @attribute-updated="onDeploymentAttributeUpdated()"
            @attribute-deleted="onDeploymentAttributeDeleted()"
          />
        </div>
        <Button
          v-if="!addingNewDeploymentAttribute"
          class="mt-3"
          label="New"
          severity="secondary"
          icon="pi pi-plus"
          @click="addingNewDeploymentAttribute = true"
        />
      </Panel>
    </div>
  </div>
</template>
