<script setup lang="ts">
import Button from 'primevue/button'
import Card from 'primevue/card'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import { useConfirm } from 'primevue/useconfirm'
import { useToast } from 'primevue/usetoast'

import type ProjectSite from '@/model/ProjectSiteI.ts'
import { ref } from 'vue'

const toast = useToast()
const confirm = useConfirm()

const model = defineModel<ProjectSite>({ required: true })
const { editable = false } = defineProps<{
  editable?: boolean
}>()
const emits = defineEmits(['site-updated', 'site-deleted'])

const editableState = ref(editable)

const createSite = () => {
  fetch(`/api/projects/${model.value.projectId}/sites`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(model.value)
  })
    .then(response => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error('Failed to create site')
      }
    })
    .then(data => {
      editableState.value = false
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: `Site #${model.value.id} ${model.value.name} created`,
        life: 3000
      })
      emits('site-updated', data)
    })
    .catch(error => {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: error.message,
        life: 3000
      })
      console.error(error)
    })
    .finally(() => {

    })
}

const updateSite = () => {
  fetch(`/api/projects/${model.value.projectId}/sites/${model.value.id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(model.value)
  })
    .then(response => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error('Failed to update site')
      }
    })
    .then(data => {
      editableState.value = false
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: `Site #${model.value.id} ${model.value.name} updated`,
        life: 3000
      })
      emits('site-updated', data)
    })
    .catch(error => {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: error.message,
        life: 3000
      })
      console.error(error)
    })
    .finally(() => {

    })
}

const createOrUpdateSite = () => {
  model.value.id > 0 ? updateSite() : createSite()
}

const deleteSite = () => {
  fetch(`/api/projects/${model.value.projectId}/sites/${model.value.id}`, {
    method: 'DELETE'
  })
    .then(response => {
      if (response.ok) {
        return
      } else {
        throw new Error('Failed to delete site')
      }
    })
    .then(data => {
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: `Site #${model.value.id} ${model.value.name} deleted`,
        life: 3000
      })
      emits('site-deleted', model.value.id)
    })
    .catch(error => {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: error.message,
        life: 3000
      })
      console.error(error)
    })
}

const confirmDelete = () => {
  if (model.value.id < 1) {
    emits('site-deleted', model.value.id)
    return
  }
  confirm.require({
    message: `Delete site #${model.value.id} ${model.value.name}?`,
    header: 'Confirmation',
    icon: 'pi pi-info-circle',
    rejectLabel: 'Cancel',
    rejectProps: {
      label: 'Cancel',
      severity: 'secondary',
      outlined: true
    },
    acceptProps: {
      label: 'Delete',
      severity: 'danger'
    },
    accept: () => {
      deleteSite()
    }
  })
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
