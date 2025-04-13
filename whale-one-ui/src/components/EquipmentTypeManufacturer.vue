<script setup lang="ts">
import AutoComplete from 'primevue/autocomplete'
import Button from 'primevue/button'
import Card from 'primevue/card'
import InputText from 'primevue/inputtext'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import { useConfirm } from 'primevue/useconfirm'

import { deleteConfirm } from '@/utils/confirms.ts'

const confirm = useConfirm()

const model = defineModel<{ name: string; models: string[] }>({ required: true })
const { editable = false } = defineProps<{
  editable?: boolean
}>()
const emits = defineEmits(['manufacturer-deleted'])

const confirmDelete = () => {
  confirm.require(
    deleteConfirm(`Delete manufacturer ${model.value.name}?`, () => emits('manufacturer-deleted')),
  )
}
</script>

<template>
  <Card class="border hover:border-surface-500 dark:border-surface-700">
    <template #content>
      <Fluid>
        <div class="flex gap-2">
          <div class="flex-grow grid grid-cols-1 sm:grid-cols-4 gap-y-3 sm:gap-3">
            <FloatLabel
              variant="on"
              class="col-span-1"
            >
              <InputText
                id="name"
                v-model="model.name"
                :disabled="!editable"
              />
              <label for="name">Name</label>
            </FloatLabel>

            <FloatLabel
              variant="on"
              class="col-span-3"
            >
              <AutoComplete
                id="options"
                v-model="model.models"
                multiple
                fluid
                :typeahead="false"
                :disabled="!editable"
              />
              <label for="options">Models</label>
            </FloatLabel>
          </div>
          <Button
            variant="text"
            size="small"
            severity="secondary"
            icon="pi pi-trash"
            :class="{ 'hover:!text-red-600': editable }"
            :disabled="!editable"
            @click="confirmDelete()"
          />
        </div>
      </Fluid>
    </template>
  </Card>
</template>
