<script setup lang="ts">
import AutoComplete from 'primevue/autocomplete'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import MultiSelect from 'primevue/multiselect'
import Select from 'primevue/select'

import {
  type AttributeFilterModel,
  type AttributeTypeModel,
  FilterOperation,
} from '@/model/AttributeTypeModel.ts'

defineProps<{ attributeType: AttributeTypeModel }>()
const model = defineModel<AttributeFilterModel>({ required: true })
const emit = defineEmits(['delete'])
</script>

<template>
  <div class="flex items-center gap-2">
    <InputText
      class="w-72"
      :value="attributeType.name"
      disabled
    />
    <Select
      class="w-44"
      v-model="model.operator"
      :options="[...Object.entries(FilterOperation).map(([key, value]) => ({ label: key, value }))]"
      option-label="label"
      option-value="value"
    />
    <template v-if="model.operator === FilterOperation.IN">
      <MultiSelect
        v-if="attributeType.type === 'select'"
        class="flex-1"
        v-model="model.value"
        :options="attributeType.metadata?.options || []"
        display="chip"
        show-clear
      />
      <AutoComplete
        v-else
        class="flex-1"
        v-model="model.value"
        multiple
        :typeahead="false"
      />
    </template>
    <template v-else>
      <InputText
        class="flex-1"
        v-model="model.value as string"
      />
    </template>

    <Button
      icon="pi pi-trash"
      size="small"
      severity="secondary"
      variant="text"
      @click="emit('delete')"
    />
  </div>
</template>
