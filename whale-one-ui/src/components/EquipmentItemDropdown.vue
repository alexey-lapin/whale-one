<script setup lang="ts">
import { ref } from 'vue'
import AutoComplete from 'primevue/autocomplete'

import { invokeEquipmentItemListGet } from '@/client/equipmentClient.ts'

import type { BaseRefModel } from '@/model/BaseModel.ts'
import type { EquipmentAssemblyPartModel } from '@/model/EquipmentModel.ts'

const props = defineProps<{
  type: BaseRefModel
}>()

const model = defineModel<EquipmentAssemblyPartModel>({ required: true })

const emit = defineEmits<{
  change: [model: BaseRefModel]
}>()

const suggestions = ref<BaseRefModel[]>([])
const selected = ref<BaseRefModel | null>(null)

const getEquipmentItems = async (q: string | null) => {
  suggestions.value = await invokeEquipmentItemListGet(props.type.id, q)
}

const onChange = (event: { value: BaseRefModel }) => {
  model.value = {
    typeId: props.type.id,
    equipmentId: event.value?.id ?? -1,
  }
  emit('change', event.value);
}
</script>

<template>
  <AutoComplete
    v-model="selected"
    :suggestions="suggestions"
    option-label="name"
    dropdown
    force-selection
    @complete="getEquipmentItems($event.query)"
    @change="onChange($event)"
  />
</template>
