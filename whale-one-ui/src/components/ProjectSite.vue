<script setup lang="ts">
import { ref } from 'vue'

import Button from 'primevue/button'
import Card from 'primevue/card'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'

import type ProjectSiteModel from '@/model/ProjectSiteModel.ts'
import { invokeSiteCreateOrUpdate, invokeSiteDelete } from '@/client/projectsiteapi.ts'
import { deleteConfirm } from '@/utils/confirms'

const toast = useToast()
const confirm = useConfirm()

const model = defineModel<ProjectSiteModel>({ required: true })
const { editable = false } = defineProps<{
  editable?: boolean
}>()
const emits = defineEmits(['site-updated', 'site-deleted'])

const editableState = ref(editable)

const createOrUpdateSite = () => {
  invokeSiteCreateOrUpdate(model.value, toast)
    .then(data => {
      editableState.value = false
      emits('site-updated', data)
    })
  // .catch(error => {
  //   toast.add(errorToast(error.message))
  //   console.error(error)
  // })
}

const deleteSite = () => {
  invokeSiteDelete(model.value, toast)
    .then(() => {
      emits('site-deleted', model.value.id)
    })
  // .catch(error => {
  //   toast.add(errorToast(error.message))
  //   console.error(error)
  // })
}

const confirmDelete = () => {
  if (model.value.id < 1) {
    emits('site-deleted', model.value.id)
    return
  }
  confirm.require(deleteConfirm(
    `Delete site #${model.value.id} ${model.value.name}?`,
    () => deleteSite()
  ))
}
</script>

<template>
  <Card class="border hover:border-surface-500">
    <template #subtitle>
      <div class="flex items-center">
        <p class="flex-grow">{{ model.id > 0 ? `#${model.id}` : 'New' }} Site</p>
        <Button variant="text" size="small" severity="secondary" icon="pi pi-pencil"
                @click="editableState=!editableState"></Button>
        <Button variant="text" size="small" severity="secondary" icon="pi pi-trash"
                class="hover:!text-red-600" @click="confirmDelete()"></Button>
      </div>
    </template>
    <template #content>
      <Fluid>
        <div class="flex flex-col gap-4">
          <FloatLabel variant="on">
            <InputText id="name" v-model="model.name" :disabled="!editableState" />
            <label for="name">Name</label>
          </FloatLabel>
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
            <FloatLabel variant="on">
              <InputNumber id="longitude" v-model="model.longitude" :disabled="!editableState" />
              <label for="longitude">Longitude</label>
            </FloatLabel>
            <FloatLabel variant="on">
              <InputNumber id="latitude" v-model="model.latitude" :disabled="!editableState" />
              <label for="latitude">Latitude</label>
            </FloatLabel>
            <FloatLabel variant="on">
              <InputNumber id="depth" v-model="model.depth" :disabled="!editableState" />
              <label for="depth">Depth</label>
            </FloatLabel>
          </div>
        </div>
      </Fluid>
      <Button v-if="editableState"
              label="Save"
              icon="pi pi-save"
              class="mt-4"
              @click="createOrUpdateSite()" />
    </template>
  </Card>
</template>
