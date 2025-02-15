<script setup lang="ts">
import { computed, type Ref, ref } from 'vue'

import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import Textarea from 'primevue/textarea'
import Select from 'primevue/select'
import Message from 'primevue/message'

import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'

const model = defineModel<string | null>({ required: true })
const { editing, equipmentTypeAttribute } = defineProps<{
  editing: boolean
  equipmentTypeAttribute: EquipmentTypeAttributeModel
}>()

const options: Ref<string[]> = ref([])

if (equipmentTypeAttribute.type === 'select' && equipmentTypeAttribute.metadata?.options) {
  options.value = equipmentTypeAttribute.metadata.options as string[]
}

const number = computed({
  get() {
    return model.value === null ? null : Number(model.value)
  },
  set(value: number | null) {
    model.value = value === null ? null : value.toString()
  },
})
</script>
<template>
  <Fluid>
    <div class="flex flex-col gap-0">
      <FloatLabel variant="on">
        <InputText
          v-if="equipmentTypeAttribute.type == 'text'"
          :id="`${equipmentTypeAttribute.id}`"
          v-model="model"
          :disabled="!editing"
        />
        <InputNumber
          v-else-if="equipmentTypeAttribute.type == 'number'"
          :id="`${equipmentTypeAttribute.id}`"
          v-model="number"
          :disabled="!editing"
        />
        <Textarea
          v-else-if="equipmentTypeAttribute.type == 'textarea'"
          :id="`${equipmentTypeAttribute.id}`"
          v-model="model"
          :disabled="!editing"
        />
        <Select
          v-else-if="equipmentTypeAttribute.type == 'select'"
          :id="`${equipmentTypeAttribute.id}`"
          v-model="model"
          :options="options"
          show-clear
          :disabled="!editing"
        />
        <label :for="`${equipmentTypeAttribute.id}`">{{ equipmentTypeAttribute.name }}</label>
      </FloatLabel>
      <Message
        severity="secondary"
        size="small"
        variant="simple"
      >
        {{ equipmentTypeAttribute.description }}
      </Message>
    </div>
  </Fluid>
</template>
