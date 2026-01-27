<script setup lang="ts">
import { onMounted, ref, type Ref, watch } from 'vue'

import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import Message from 'primevue/message'

import EquipmentItemDropdown from '@/components/EquipmentItemDropdown.vue'

import { invokeEquipmentTypeGet } from '@/client/equipmentTypeClient.ts'

import type { EquipmentTypeModel } from '@/model/EquipmentTypeModel.ts'
import { type EquipmentNewModel, EquipmentStatus } from '@/model/EquipmentModel.ts'
import type { BaseRefModel } from '@/model/BaseModel.ts'
import { invokeEquipmentCreate, invokeEquipmentFindByDescriptor } from '@/client/equipmentClient.ts'

const props = defineProps<{ type: BaseRefModel }>()

const model: Ref<EquipmentNewModel> = ref({
  name: null,
  type: props.type,
  status: EquipmentStatus.AVAILABLE,
  assemblyScope: 'SESSION',
})

const emit = defineEmits<{
  assembled: [id: number]
}>()

const equipmentType: Ref<EquipmentTypeModel | null> = ref(null)

const exists: Ref<boolean | null> = ref(null)
const id: Ref<BaseRefModel | null> = ref(null)
const nameParts = ref<string[]>([])

const loading = ref(false)

const getEquipmentType = () => {
  return invokeEquipmentTypeGet(props.type.id)
    .then((data) => (equipmentType.value = data))
    .then(
      () =>
        (model.value.assemblyParts = equipmentType.value?.metadata?.assemblyParts?.map((part) => {
          return {
            typeId: part.id,
            equipmentId: -1,
          }
        })),
    )
    .then(
      () =>
        (nameParts.value =
          equipmentType.value?.metadata?.assemblyParts?.map(() => '') ?? ([] as string[])),
    )
    .catch(() => {})
}

watch(
  () => model.value.assemblyParts,
  async (newModel) => {
    console.log('watching')
    if (newModel?.every((part) => part.equipmentId > 0)) {
      const descriptor = newModel
        .map((m) => m.equipmentId)
        .sort()
        .join('-')
      exists.value = await invokeEquipmentFindByDescriptor(descriptor)
        .then((data) => (id.value = { id: data.id, name: data.name }))
        .then(() => true)
        .catch(() => false)
    } else {
      exists.value = null
    }
  },
  { deep: true },
)

const create = () => {
  loading.value = true
  model.value.name = nameParts.value.join(' - ')
  invokeEquipmentCreate(model.value)
    .then((data) => (id.value = { id: data.id, name: data.name }))
    .then(() => (exists.value = true))
    .finally(() => (loading.value = false))
}

const add = () => {
  if (id.value) {
    loading.value = true
    emit('assembled', id.value.id)
  }
}

onMounted(() => {
  getEquipmentType()
})
</script>

<template>
  <div class="flex flex-col gap-3 mt-1">
    <template
      v-if="model.assemblyParts"
      v-for="(part, index) in equipmentType?.metadata?.assemblyParts"
    >
      <FloatLabel
        v-if="model.assemblyParts[index]"
        variant="on"
      >
        <EquipmentItemDropdown
          v-model="model.assemblyParts[index]"
          :type="part"
          @change="(e) => (nameParts[index] = e.name)"
        />
        <label>{{ part.name }}</label>
      </FloatLabel>
    </template>

    <template v-if="exists === true">
      <Message severity="success"
        >Assembly exists: {{ id?.name }}
        <router-link
          v-slot="{ href, navigate }"
          :to="`/equipment/${id?.id}`"
        >
          <a
            :href="href"
            @click="navigate"
          >
            <Button
              icon="pi pi-external-link"
              severity="success"
              variant="text"
              size="small"
            />
          </a>
        </router-link>
      </Message>
      <Button
        label="Add"
        :loading="loading"
        @click="add()"
      />
    </template>

    <template v-if="exists === false">
      <Message severity="warn">Assembly does not exist</Message>
      <Button
        label="Create"
        :loading="loading"
        @click="create()"
      />
    </template>
  </div>
</template>
