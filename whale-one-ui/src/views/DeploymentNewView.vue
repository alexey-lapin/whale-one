<script setup lang="ts">
import { ref } from 'vue'

import AutoComplete from 'primevue/autocomplete'
import Button from 'primevue/button'
import router from '@/router'
import FloatLabel from 'primevue/floatlabel'
import Textarea from 'primevue/textarea'
import InputText from 'primevue/inputtext'
import { useToast } from 'primevue/usetoast'

const toast = useToast()

const model = ref({
  id: 0,
  version: 0,
  projectId: null,
  siteId: null,
  name: null,
  description: null
})

const projects = ref([])
const sites = ref([])

const loading = ref(false)

const create = () => {
  loading.value = true
  fetch('/api/deployments', {
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
        throw new Error('Failed to create project')
      }
    })
    .then(data => {
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: `Project #${data.id} ${data.name} created`,
        life: 3000
      })
      return router.push(`/deployments/${data.id}`)
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
      loading.value = false
    })
}

const projectItems = (q: string) => {
  fetch(`/api/projects/items?q=${q}`)
    .then(response => response.json())
    .then(data => projects.value = data)
    .catch(error => console.error(error))
}

const siteItems = (projectId: number, q: string) => {
  fetch(`/api/projects/${projectId}/sites/items?q=${q}`)
    .then(response => response.json())
    .then(data => sites.value = data)
    .catch(error => console.error(error))
}
</script>

<template>
  <div class="mt-5">
    <h1 class="text-xl">Create New Deployment</h1>
    <div class="flex flex-col gap-5 my-4">
      <FloatLabel variant="on" class="w-full">
        <InputText id="name" class="w-full" v-model="model.name" />
        <label for="name">Name</label>
      </FloatLabel>

      <FloatLabel variant="on" class="w-full">
        <Textarea class="w-full" v-model="model.description" placeholder1="Description" />
        <label for="1name">Description</label>
      </FloatLabel>

      <FloatLabel variant="on" class="w-full">
        <AutoComplete v-model="model.projectId" dropdown :suggestions="projects"
                      option-label="name"
                      force-selection
                      @change="model.siteId = null"
                      @complete="projectItems($event.query)" />
        <label for="1name">Project</label>
      </FloatLabel>

      <FloatLabel variant="on" class="w-full">
        <AutoComplete v-model="model.siteId" dropdown :suggestions="sites"
                      option-label="name"
                      force-selection
                      @complete="siteItems((model.projectId as any).id, $event.query)" />
        <label for="1name">Site</label>
      </FloatLabel>
    </div>
    <Button label="Create" icon="pi pi-plus" :loading="loading" @click="create()"></Button>
  </div>
</template>
