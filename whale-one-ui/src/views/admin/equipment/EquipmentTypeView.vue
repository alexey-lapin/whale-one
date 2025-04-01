<script setup lang="ts">
import { onMounted, type Ref, ref, watch } from 'vue'

import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import InputText from 'primevue/inputtext'
import Panel from 'primevue/panel'
import Textarea from 'primevue/textarea'

import EquipmentTypeAttribute from '@/components/EquipmentTypeAttribute.vue'
import EquipmentTypeManufacturer from '@/components/EquipmentTypeManufacturer.vue'

import { invokeEquipmentTypeGet, invokeEquipmentTypeUpdate } from '@/client/equipmentTypeClient.ts'
import { invokeAttributeListGet } from '@/client/equipmentTypeAttributeClient.ts'

import type {
  EquipmentTypeManufacturerModel,
  EquipmentTypeModel,
} from '@/model/EquipmentTypeModel.ts'
import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'
import EntityHeaderDialog from '@/components/EntityHeaderDialog.vue'

const props = defineProps<{
  id: number
}>()

const model: Ref<EquipmentTypeModel | null> = ref(null)

const equipmentAttributes: Ref<EquipmentTypeAttributeModel[]> = ref([])
const deploymentAttributes: Ref<EquipmentTypeAttributeModel[]> = ref([])

const newEquipmentAttribute: Ref<EquipmentTypeAttributeModel> = ref({
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
const editingInfo = ref(false)
const editingManufacturers = ref(false)
const addingNewEquipmentAttribute = ref(false)
const addingNewDeploymentAttribute = ref(false)

const getEquipmentType = () => {
  invokeEquipmentTypeGet(props.id)
    .then((data) => (model.value = data))
    .catch(() => {})
}

const getEquipmentAttributes = () => {
  invokeAttributeListGet('equipment', props.id)
    .then((data) => (equipmentAttributes.value = data))
    .catch(() => {})
}

const getDeploymentAttributes = () => {
  invokeAttributeListGet('deployment', props.id)
    .then((data) => (deploymentAttributes.value = data))
    .catch(() => {})
}

const updateEquipmentType = () => {
  if (!model.value) {
    return
  }
  if (editingManufacturers.value) {
    updateManufacturersInModel()
  }
  loading.value = true
  invokeEquipmentTypeUpdate(model.value)
    .then((data) => {
      model.value = data
      editingInfo.value = false
      editingManufacturers.value = false
    })
    .catch(() => {})
    .finally(() => {
      loading.value = false
    })
}

const onEquipmentAttributeUpdated = () => {
  getEquipmentAttributes()
  addingNewEquipmentAttribute.value = false
}

const onEquipmentAttributeDeleted = () => {
  addingNewEquipmentAttribute.value = false
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
  getEquipmentAttributes()
  getDeploymentAttributes()
})

const manufacturers = ref<EquipmentTypeManufacturerModel[]>([])

const ensureMetadata = () => {
  if (model.value == null) {
    return []
  }
  if (!model.value.metadata) {
    model.value.metadata = {}
  }
  if (!model.value.metadata.manufacturers) {
    model.value.metadata.manufacturers = []
  }
  return model.value.metadata.manufacturers as EquipmentTypeManufacturerModel[]
}

watch(
  () => model.value,
  () => {
    manufacturers.value = ensureMetadata()
  },
  { immediate: true, deep: true },
)

const updateManufacturersInModel = () => {
  if (!model.value) {
    return
  }
  ensureMetadata()
  model.value.metadata!.manufacturers = cleanAndSortManufacturers([...manufacturers.value])
}

const cleanAndSortManufacturers = (manufacturers: EquipmentTypeManufacturerModel[]) => {
  const cleaned = manufacturers.filter((m) => m.name?.trim())

  cleaned.sort((a, b) => (a.name || '').localeCompare(b.name || ''))

  cleaned.forEach((manufacturer) => {
    manufacturer.models = (manufacturer.models || [])
      .filter((model) => model?.trim())
      .sort((a, b) => (a || '').localeCompare(b || ''))
  })

  return cleaned
}

const addManufacturer = () => {
  manufacturers.value.push({
    name: '',
    models: [],
  })
}
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
          <span
            class="p-panel-title cursor-pointer"
            @click="toggle()"
          >
            Equipment Type
          </span>
        </EntityHeaderDialog>
      </template>
      <template #icons>
        <div class="flex gap-2">
          <Button
            icon="pi pi-pencil"
            severity="secondary"
            size="small"
            variant="text"
            @click="editingInfo = !editingInfo"
          />
        </div>
      </template>
      <template #default>
        <Fluid>
          <div class="mt-1 flex flex-col gap-3">
            <FloatLabel variant="on">
              <InputText
                id="name"
                v-model="model.name"
                :disabled="!editingInfo"
              />
              <label for="name">Name</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <Textarea
                v-model="model.description"
                :disabled="!editingInfo"
              />
              <label for="1name">Description</label>
            </FloatLabel>

            <div class="flex items-center gap-2">
              <Checkbox
                binary
                input-id="assembly"
              />
              <label for="assembly">Assembly</label>
            </div>
          </div>
        </Fluid>
        <Button
          v-if="editingInfo"
          label="Save"
          icon="pi pi-save"
          class="mt-4"
          :loading="loading"
          @click="updateEquipmentType()"
        />
      </template>
    </Panel>

    <Panel header="Manufacturers">
      <template #icons>
        <div class="flex gap-2">
          <Button
            icon="pi pi-pencil"
            severity="secondary"
            size="small"
            variant="text"
            @click="editingManufacturers = !editingManufacturers"
          />
        </div>
      </template>
      <template #default>
        <div class="mt-1 flex flex-col gap-3">
          <EquipmentTypeManufacturer
            v-for="(manufacturer, index) in manufacturers"
            v-model="manufacturers[index]"
            :editable="editingManufacturers"
            @manufacturer-deleted="manufacturers.splice(index, 1)"
          />
        </div>
        <div class="flex gap-2">
          <Button
            v-if="editingManufacturers"
            label="Save"
            icon="pi pi-save"
            class="mt-3"
            @click="updateEquipmentType()"
          />
          <Button
            v-if="editingManufacturers"
            label="New"
            icon="pi pi-plus"
            severity="secondary"
            class="mt-3"
            @click="addManufacturer()"
          />
        </div>
      </template>
    </Panel>

    <Panel
      header="Equipment Attributes"
      toggleable
    >
      <div class="mt-1 flex flex-col gap-3">
        <EquipmentTypeAttribute
          v-for="(attribute, index) in equipmentAttributes"
          attribute-entity="equipment"
          :key="attribute.id"
          :modelValue="attribute"
          @attribute-deleted="getEquipmentAttributes()"
        />

        <EquipmentTypeAttribute
          v-if="addingNewEquipmentAttribute"
          v-model="newEquipmentAttribute"
          attribute-entity="equipment"
          :editable="true"
          @attribute-updated="onEquipmentAttributeUpdated()"
          @attribute-deleted="onEquipmentAttributeDeleted()"
        />
      </div>
      <Button
        v-if="!addingNewEquipmentAttribute"
        class="mt-3"
        label="New"
        severity="secondary"
        icon="pi pi-plus"
        @click="addingNewEquipmentAttribute = true"
      />
    </Panel>

    <Panel
      header="Deployment Attributes"
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
</template>
