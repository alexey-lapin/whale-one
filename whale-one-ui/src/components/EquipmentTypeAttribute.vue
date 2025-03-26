<script setup lang="ts">
import { computed, ref } from 'vue'

import AutoComplete from 'primevue/autocomplete'
import Button from 'primevue/button'
import Card from 'primevue/card'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import { useConfirm } from 'primevue/useconfirm'

import {
  type AttributeEntity,
  invokeAttributeCreateOrUpdate,
  invokeAttributeDelete,
} from '@/client/equipmentTypeAttributeClient.ts'
import { deleteConfirm } from '@/utils/confirms'

import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'

const confirm = useConfirm()

const model = defineModel<EquipmentTypeAttributeModel>({ required: true })
const { attributeEntity, editable = false } = defineProps<{
  attributeEntity: AttributeEntity
  editable?: boolean
}>()
const emits = defineEmits(['attribute-updated', 'attribute-deleted'])

const editableState = ref(editable)
const idVisible = ref(false)

const header = computed(() => {
  const base = 'Attribute'
  if (model.value.id < 1) {
    return `New ${base}`
  }
  return idVisible.value ? `${base} #${model.value.id} ` : base
})

const attributeTypes = [
  { label: 'number', value: 'number' },
  { label: 'select', value: 'select' },
  { label: 'text', value: 'text' },
  { label: 'textarea', value: 'textarea' },
  { label: 'files', value: 'files' },
]

const createOrUpdateAttribute = () => {
  invokeAttributeCreateOrUpdate(attributeEntity, model.value)
    .then((data) => {
      editableState.value = false
      emits('attribute-updated', data)
    })
    .catch(() => {})
}

const deleteAttribute = () => {
  invokeAttributeDelete(attributeEntity, model.value)
    .then(() => {
      emits('attribute-deleted', model.value.id)
    })
    .catch(() => {})
}

const confirmDelete = () => {
  if (model.value.id < 1) {
    emits('attribute-deleted', model.value.id)
    return
  }
  confirm.require(
    deleteConfirm(`Delete attribute #${model.value.id} ${model.value.name}?`, () =>
      deleteAttribute(),
    ),
  )
}

const onTypeChange = () => {
  if (model.value.type === 'select' && !model.value.metadata) {
    model.value.metadata = { options: [] }
  }
}
</script>

<template>
  <Card class="border hover:border-surface-500">
    <template #subtitle>
      <div class="flex items-center justify-between gap-2">
        <span @click="idVisible = !idVisible">{{ header }}</span>
        <div>
          <Button
            variant="text"
            size="small"
            severity="secondary"
            icon="pi pi-pencil"
            @click="editableState = !editableState"
          />
          <Button
            variant="text"
            size="small"
            severity="secondary"
            icon="pi pi-trash"
            class="hover:!text-red-600"
            @click="confirmDelete()"
          />
        </div>
      </div>
    </template>
    <template #content>
      <Fluid>
        <div class="flex flex-col gap-4">
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-y-3 sm:gap-3">
            <FloatLabel variant="on">
              <Select
                id="type"
                v-model="model.type"
                :options="attributeTypes"
                option-label="label"
                option-value="value"
                :disabled="!editableState"
                @change="onTypeChange()"
              />
              <label for="type">Type</label>
            </FloatLabel>

            <FloatLabel
              variant="on"
              class="col-span-2"
            >
              <InputText
                id="name"
                v-model="model.name"
                :disabled="!editableState"
              />
              <label for="name">Name</label>
            </FloatLabel>
          </div>

          <FloatLabel variant="on">
            <Textarea
              id="name"
              v-model="model.description"
              :disabled="!editableState"
            />
            <label for="name">Description</label>
          </FloatLabel>

          <div v-if="model.type === 'select' && model.metadata">
            <FloatLabel variant="on">
              <AutoComplete
                id="options"
                v-model="model.metadata.options"
                multiple
                fluid
                :typeahead="false"
                :disabled="!editableState"
              />
              <label for="options">Options</label>
            </FloatLabel>
          </div>
        </div>
      </Fluid>

      <Button
        v-if="editableState"
        label="Save"
        icon="pi pi-save"
        class="mt-4"
        @click="createOrUpdateAttribute()"
      />
    </template>
  </Card>
</template>
