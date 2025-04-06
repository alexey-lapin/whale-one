<script setup lang="ts">
import { onMounted, ref, type Ref, useTemplateRef } from 'vue'

import Button from 'primevue/button'
import Select from 'primevue/select'
import { useSortable } from '@vueuse/integrations/useSortable'

import EquipmentTypeTag from '@/components/EquipmentTypeTag.vue'
import { invokeEquipmentTypeItemListGet } from '@/client/equipmentTypeClient.ts'

import type { BaseRefModel } from '@/model/BaseModel.ts'

const model = defineModel<BaseRefModel[]>({ required: true })

const equipmentTypes: Ref<BaseRefModel[]> = ref([])

const partEquipmentType: Ref<BaseRefModel | null> = ref(null)

const el = useTemplateRef<HTMLElement>('el')
useSortable(el, model, {
  handle: '.handle',
})

const equipmentTypeItems = (q: string | null) => {
  invokeEquipmentTypeItemListGet(q)
    .then((data) => (equipmentTypes.value = data))
    .catch(() => {})
}

const addPartEquipmentType = () => {
  if (partEquipmentType.value) {
    model.value.push(partEquipmentType.value)
    partEquipmentType.value = null
  }
}

const removePartEquipmentType = (index: number) => {
  model.value.splice(index, 1)
}

onMounted(() => {
  equipmentTypeItems(null)
})
</script>

<template>
  <div class="flex flex-col gap-2">
    <div
      id="ll"
      ref="el"
      class="flex flex-col"
    >
      <template
        v-for="(part, index) in model"
        :key="part.id"
      >
        <div class="flex items-center gap-2">
          <i class="pi pi-bars handle cursor-pointer" />
          <EquipmentTypeTag :name="part.name" />
          <Button
            icon="pi pi-trash"
            severity="secondary"
            size="small"
            variant="text"
            @click="removePartEquipmentType(index)"
          />
        </div>
      </template>
    </div>
    <div class="flex items-center gap-2">
      <Select
        v-model="partEquipmentType"
        :options="equipmentTypes"
        option-label="name"
        :fluid="false"
      />
      <Button
        icon="pi pi-plus"
        severity="secondary"
        @click="addPartEquipmentType()"
      />
    </div>
  </div>
</template>
