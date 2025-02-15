<script setup lang="ts">
import { computed, onMounted, ref, type Ref } from 'vue'
import Button from 'primevue/button'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import Panel from 'primevue/panel'
import ToggleButton from 'primevue/togglebutton'
import { useToast } from 'primevue/usetoast'
import dayjs from 'dayjs'

import EquipmentAttribute from '@/components/EquipmentAttribute.vue'
import { errorToast, successToast } from '@/utils/toasts.ts'

import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'
import type { EquipmentAttributeModel, EquipmentModel } from '@/model/EquipmentModel.ts'

const toast = useToast()

const props = defineProps<{
  id: number
}>()

const model: Ref<EquipmentModel> = ref({
  id: 0,
  version: 0,
  createdAt: '',
  createdBy: {
    id: 0,
    name: '',
  },
  active: false,
  name: '',
  typeId: -1,
  type: {
    id: 0,
    name: '',
  },
  attributes: [],
})

const equipmentTypeAttributes: Ref<EquipmentTypeAttributeModel[]> = ref([])
const attributes: Ref<EquipmentAttributeModel[]> = ref([])

const loading = ref(false)
const editing = ref(false)

const variant = computed(() => (editing.value ? 'outlined' : 'filled'))

const getEquipment = () => {
  return fetch(`/api/equipment/${props.id}`)
    .then((response) => response.json())
    .then((data) => (model.value = data))
    .catch((error) => console.error(error))
}

const getEquipmentTypeAttributes = () => {
  return fetch(`/api/equipment/types/${model.value.type.id}/attributes`)
    .then((response) => response.json())
    .then((data) => (equipmentTypeAttributes.value = data))
    .then(() => (attributes.value = collectAttributes()))
    .catch((error) => console.error(error))
}

function collectAttributes(): EquipmentAttributeModel[] {
  return equipmentTypeAttributes.value.map((attribute) => ({
    id: 0,
    equipmentTypeAttributeId: attribute.id,
    value:
      model.value.attributes.find((a) => a.equipmentTypeAttributeId === attribute.id)?.value ??
      null,
  }))
}

const updateEquipment = () => {
  loading.value = true
  model.value.attributes = attributes.value.filter((a) => a.value)
  fetch(`/api/equipment/${props.id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(model.value),
  })
    .then((response) => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error('Failed to update equipment')
      }
    })
    .then((data) => {
      model.value = data
      model.value.attributes = collectAttributes()
      toast.add(successToast(`Equipment #${data.id} ${data.name} updated`))
      editing.value = false
    })
    .catch((error) => {
      toast.add(errorToast(error.message))
      console.error(error)
    })
    .finally(() => {
      loading.value = false
    })
}

onMounted(() => {
  getEquipment()
    .then(() => getEquipmentTypeAttributes())
    .then(() => {
      console.log(model.value)
      console.log(equipmentTypeAttributes.value)
    })
})
</script>

<template>
  <div class="mt-5">
    <h1 class="text-xl">Equipment</h1>
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
            <ToggleButton
              v-model="model.active"
              off-label="Inactive"
              on-label="Active"
              size="small"
            />
            <Button
              severity="secondary"
              size="small"
              icon="pi pi-pencil"
              @click="editing = !editing"
            >
            </Button>
          </div>
        </template>
        <template #default>
          <div class="mt-1 flex flex-col gap-4">
            <FloatLabel
              variant="on"
              class="w-full"
            >
              <InputText
                id="name"
                class="w-full"
                v-model="model.name"
                :disabled="!editing"
                :variant="variant"
              />
              <label for="name">Name</label>
            </FloatLabel>

            <!--          <FloatLabel variant="on" class="w-full">-->
            <!--            <Textarea class="w-full" v-model="project.description" />-->
            <!--            <label for="1name">Description</label>-->
            <!--          </FloatLabel>-->

            <template v-if="attributes.length > 0">
              <template v-for="(attribute, index) in equipmentTypeAttributes">
                <EquipmentAttribute
                  v-model="attributes[index].value"
                  :equipment-type-attribute="attribute"
                  :editing="editing"
                />
              </template>
            </template>
          </div>
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
  </div>
</template>
