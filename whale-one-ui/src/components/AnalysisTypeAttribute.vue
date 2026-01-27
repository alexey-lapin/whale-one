<script setup lang="ts">
import AutoComplete from 'primevue/autocomplete'
import Button from 'primevue/button'
import Card from 'primevue/card'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import { useConfirm } from 'primevue/useconfirm'

import { deleteConfirm } from '@/utils/confirms.ts'

import type { AnalysisTypeAttributeModel } from '@/model/AnalysisTypeModel.ts'

const confirm = useConfirm()

const model = defineModel<AnalysisTypeAttributeModel>({ required: true })
const { editable = false } = defineProps<{
  editable?: boolean
}>()
const emits = defineEmits(['attribute-deleted'])

const header = 'Attribute'

const attributeTypes = [
  { label: 'number', value: 'number' },
  { label: 'select', value: 'select' },
  { label: 'text', value: 'text' },
  { label: 'textarea', value: 'textarea' },
  { label: 'files', value: 'files' },
]

const onTypeChange = () => {
  if (model.value.type === 'select' && !model.value.metadata) {
    model.value.metadata = { options: [] }
  }
}

const confirmDelete = () => {
  confirm.require(
    deleteConfirm(`Delete attribute ${model.value.name}?`, () => emits('attribute-deleted')),
  )
}
</script>

<template>
  <Card class="border hover:border-surface-500 dark:border-surface-700">
    <template #content>
      <Fluid>
        <div class="flex gap-2">
          <div class="grow flex flex-col gap-4">
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-y-3 sm:gap-3">
              <FloatLabel variant="on">
                <Select
                  id="type"
                  v-model="model.type"
                  :options="attributeTypes"
                  option-label="label"
                  option-value="value"
                  :disabled="!editable"
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
                  :disabled="!editable"
                />
                <label for="name">Name</label>
              </FloatLabel>
            </div>

            <FloatLabel variant="on">
              <Textarea
                id="name"
                v-model="model.description"
                :disabled="!editable"
              />
              <label for="name">Description</label>
            </FloatLabel>

            <div v-if="model.type === 'select' && model.metadata">
              <FloatLabel variant="on">
                <AutoComplete
                  id="options"
                  v-model="model.metadata.options"
                  multiple
                  :typeahead="false"
                  :disabled="!editable"
                />
                <label for="options">Options</label>
              </FloatLabel>
            </div>
          </div>
          <Button
            icon="pi pi-trash"
            variant="text"
            size="small"
            severity="secondary"
            :class="{ 'hover:text-red-600!': editable }"
            :disabled="!editable"
            @click="confirmDelete()"
          />
        </div>
      </Fluid>
    </template>
  </Card>
</template>
