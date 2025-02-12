<script setup lang="ts">
import { computed, type Ref, ref } from 'vue'
import Fluid from 'primevue/fluid'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import Select from 'primevue/select'
import Message from 'primevue/message'

import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'

const model = defineModel<string>({ required: true })
const { editable = false, equipmentTypeAttribute } = defineProps<{
  editable?: boolean
  equipmentTypeAttribute: EquipmentTypeAttributeModel
}>()
const emits = defineEmits(['attribute-updated', 'attribute-deleted'])

const editableState = ref(editable)

const options: Ref<string[]> = ref([])

if (equipmentTypeAttribute.type === 'select' && equipmentTypeAttribute.metadata?.options) {
  options.value = equipmentTypeAttribute.metadata.options as string[]
}

const number = computed({
  get() {
    return Number(model.value)
  },
  set(value: number) {
    model.value = value.toString()
  }
})

</script>
<template>
  <!--  <Card class="border hover:border-surface-500">-->
  <!--    <template #subtitle>-->
  <!--      <div class="flex items-center">-->
  <!--        <p class="flex-grow">{{ equipmentTypeAttribute.id }} Attribute</p>-->
  <!--        <Button variant="text" size="small" severity="secondary" icon="pi pi-pencil"-->
  <!--                @click="editableState=!editableState"></Button>-->
  <!--        <Button variant="text" size="small" severity="secondary" icon="pi pi-trash"-->
  <!--                class="hover:!text-red-600" @click="confirmDelete()"></Button>-->
  <!--      </div>-->
  <!--    </template>-->
  <!--    <template #content>-->
  <Fluid>
    <div class="flex flex-col gap-0">
      <!--          <FloatLabel variant="on">-->
      <label :for="`${equipmentTypeAttribute.id}`">{{ equipmentTypeAttribute.name }}</label>

      <InputText v-if="equipmentTypeAttribute.type == 'text'"
                 :id="`${equipmentTypeAttribute.id}`"
                 v-model="model"
                 :disabled="editableState" />
      <InputNumber v-else-if="equipmentTypeAttribute.type == 'number'"
                   :id="`${equipmentTypeAttribute.id}`"
                   v-model="number"
                   :disabled="editableState" />
      <Textarea v-else-if="equipmentTypeAttribute.type == 'textarea'"
                :id="`${equipmentTypeAttribute.id}`"
                v-model="model"
                :disabled="editableState" />
      <Select v-else-if="equipmentTypeAttribute.type == 'select'"
              :id="`${equipmentTypeAttribute.id}`"
              v-model="model"
              :options="options"
              :disabled="editableState" />
      <!--          </FloatLabel>-->
      <Message severity="secondary" size="small" variant="simple">
        {{ equipmentTypeAttribute.description }}
      </Message>
      <!--          <FloatLabel variant="on">-->
      <!--            <Textarea id="name" v-model="model.description" :disabled="!editableState" />-->
      <!--            <label for="name">Description</label>-->
      <!--          </FloatLabel>-->

    </div>
  </Fluid>
  <!--      <Button v-if="editableState"-->
  <!--              label="Save"-->
  <!--              icon="pi pi-save"-->
  <!--              class="mt-4"-->
  <!--              @click="createOrUpdateAttribute()" />-->
  <!--    </template>-->
  <!--  </Card>-->
</template>
