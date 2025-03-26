<script setup lang="ts">
import { computed, ref } from 'vue'

import Button from 'primevue/button'
import Card from 'primevue/card'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import { useConfirm } from 'primevue/useconfirm'

import { invokeSiteCreateOrUpdate, invokeSiteDelete } from '@/client/projectClient.ts'
import { deleteConfirm } from '@/utils/confirms'

import type { ProjectSiteModel } from '@/model/ProjectModel.ts'

const confirm = useConfirm()

const model = defineModel<ProjectSiteModel>({ required: true })
const { editable = false } = defineProps<{
  editable?: boolean
}>()
const emits = defineEmits(['site-updated', 'site-deleted'])

const editableState = ref(editable)
const idVisible = ref(false)

const header = computed(() => {
  const base = 'Site'
  if (model.value.id < 1) {
    return `New ${base}`
  }
  return idVisible.value ? `${base} #${model.value.id} ` : base
})

const createOrUpdateSite = () => {
  invokeSiteCreateOrUpdate(model.value)
    .then((data) => {
      editableState.value = false
      emits('site-updated', data)
    })
    .catch(() => {})
}

const deleteSite = () => {
  invokeSiteDelete(model.value)
    .then(() => {
      emits('site-deleted', model.value.id)
    })
    .catch(() => {})
}

const confirmDelete = () => {
  if (model.value.id < 1) {
    emits('site-deleted', model.value.id)
    return
  }
  confirm.require(
    deleteConfirm(`Delete site #${model.value.id} ${model.value.name}?`, () => deleteSite()),
  )
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
          <FloatLabel variant="on">
            <InputText
              id="name"
              v-model="model.name"
              :disabled="!editableState"
            />
            <label for="name">Name</label>
          </FloatLabel>
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
            <FloatLabel variant="on">
              <InputNumber
                id="longitude"
                v-model="model.longitude"
                :disabled="!editableState"
              />
              <label for="longitude">Longitude</label>
            </FloatLabel>
            <FloatLabel variant="on">
              <InputNumber
                id="latitude"
                v-model="model.latitude"
                :disabled="!editableState"
              />
              <label for="latitude">Latitude</label>
            </FloatLabel>
            <FloatLabel variant="on">
              <InputNumber
                id="depth"
                v-model="model.depth"
                :disabled="!editableState"
              />
              <label for="depth">Depth</label>
            </FloatLabel>
          </div>
        </div>
      </Fluid>
      <Button
        v-if="editableState"
        label="Save"
        icon="pi pi-save"
        class="mt-4"
        @click="createOrUpdateSite()"
      />
    </template>
  </Card>
</template>
