<script setup lang="ts">
import { computed, onMounted, ref, type Ref, watch } from 'vue'

import Button from 'primevue/button'
import InputGroup from 'primevue/inputgroup'
import InputGroupAddon from 'primevue/inputgroupaddon'
import InputText from 'primevue/inputtext'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import Panel from 'primevue/panel'
import Select from 'primevue/select'
import ToggleButton from 'primevue/togglebutton'

import router from '@/router'

import EntityHeaderDialog from '@/components/EntityHeaderDialog.vue'
import EquipmentAttribute from '@/components/EquipmentAttribute.vue'
import EquipmentTypeTag from '@/components/EquipmentTypeTag.vue'

import {
  invokeEquipmentGet,
  invokeEquipmentItemGet,
  invokeEquipmentToggleActive,
  invokeEquipmentUpdate,
} from '@/client/equipmentClient.ts'
import { ApiError, ErrorClassification } from '@/client/baseClient.ts'
import { invokeEquipmentTypeGet } from '@/client/equipmentTypeClient.ts'
import { invokeAttributeListGet } from '@/client/equipmentTypeAttributeClient.ts'

import {
  type EquipmentAttributeModel,
  type EquipmentModel,
  EquipmentStatus,
} from '@/model/EquipmentModel.ts'
import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'
import type {
  EquipmentTypeManufacturerModel,
  EquipmentTypeModel,
} from '@/model/EquipmentTypeModel.ts'
import type { BaseRefModel } from '@/model/BaseModel.ts'
import MessageStaleVersion from '@/components/MessageStaleVersion.vue'

const props = defineProps<{
  id: number
}>()

const model: Ref<EquipmentModel | null> = ref(null)
const versionStale = ref(false)
const active = ref(false)

const equipmentType: Ref<EquipmentTypeModel | null> = ref(null)
const equipmentTypeAttributes: Ref<EquipmentTypeAttributeModel[]> = ref([])
const attributes: Ref<EquipmentAttributeModel[]> = ref([])
const assemblyParts: Ref<BaseRefModel[]> = ref([])

const loading = ref(false)
const editing = ref(false)

const getEquipment = () => {
  return invokeEquipmentGet(props.id)
    .then((data) => (model.value = data))
    .then(() => (active.value = model.value?.active ?? false))
    .catch(() => {})
}

const getEquipmentType = () => {
  if (!model.value) {
    return Promise.resolve()
  }
  return invokeEquipmentTypeGet(model.value.type.id)
    .then((data) => (equipmentType.value = data))
    .catch(() => {})
}

const getEquipmentAssemblyParts = () => {
  if (!model.value || !equipmentType.value) {
    return Promise.resolve()
  }
  if (equipmentType.value.isAssembly && model.value.assemblyParts) {
    assemblyParts.value = []
    return Promise.all(
      model.value.assemblyParts.map((part) => {
        return invokeEquipmentItemGet(part.equipmentId)
          .then((data) => {
            assemblyParts.value.push(data)
          })
          .catch(() => {})
      }),
    )
  } else {
    return Promise.resolve()
  }
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
    .catch((error) => {
      if (error instanceof ApiError) {
        if (
          error.problemDetails?.classification === ErrorClassification.OPTIMISTIC_LOCKING_FAILURE
        ) {
          versionStale.value = true
        }
      }
    })
    .finally(() => (loading.value = false))
}

const toggleActive = () => {
  if (!model.value) {
    return
  }
  loading.value = true
  invokeEquipmentToggleActive(model.value.id)
    .then((data) => (model.value = data))
    .catch(() => {
      if (model.value) {
        active.value = model.value.active
      }
    })
    .finally(() => (loading.value = false))
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

const loadData = () => {
  getEquipment().then(() => {
    getEquipmentType().then(() => {
      getEquipmentAssemblyParts()
    })
    getEquipmentTypeAttributes()
  })
}

watch(
  () => props.id,
  (newValue, oldValue) => {
    if (!newValue) {
      return
    }
    if (newValue !== oldValue) {
      loadData()
    }
  },
)

onMounted(() => {
  loadData()
})
</script>

<template>
  <MessageStaleVersion
    class="mt-3"
    :model-value="versionStale"
  />

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
            v-model="active"
            off-label="Inactive"
            on-label="Active"
            size="small"
            @click="toggleActive()"
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
                id="status"
                v-model="model.status"
                :options="Object.values(EquipmentStatus)"
                :disabled="!editing"
              />
              <label>Status</label>
            </FloatLabel>

            <template v-if="equipmentType?.isAssembly === true">
              <template
                v-if="model.assemblyParts?.length == assemblyParts.length"
                v-for="(part, index) in equipmentType?.metadata?.assemblyParts"
              >
                <FloatLabel
                  v-if="assemblyParts[index]"
                  variant="on"
                >
                  <InputGroup>
                    <InputText
                      v-model="assemblyParts[index].name"
                      disabled
                    />
                    <InputGroupAddon>
                      <Button
                        icon="pi pi-external-link"
                        severity="secondary"
                        variant="text"
                        @click="router.push(`/equipment/${assemblyParts[index].id}`)"
                      />
                    </InputGroupAddon>
                  </InputGroup>
                  <label>{{ part.name }}</label>
                </FloatLabel>
              </template>
            </template>

            <template v-if="equipmentType?.isAssembly === false">
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
            </template>

            <template v-if="attributes.length > 0">
              <template v-for="(attribute, index) in equipmentTypeAttributes">
                <EquipmentAttribute
                  v-if="attributes[index]"
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
