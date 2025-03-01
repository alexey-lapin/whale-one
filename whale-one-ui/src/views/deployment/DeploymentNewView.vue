<script setup lang="ts">
import { type Ref, ref } from 'vue'

import AutoComplete from 'primevue/autocomplete'
import Button from 'primevue/button'
import router from '@/router'
import FloatLabel from 'primevue/floatlabel'
import Textarea from 'primevue/textarea'
import InputText from 'primevue/inputtext'

import { invokeProjectItemListGet, invokeSiteItemListGet } from '@/client/projectClient.ts'
import { invokeDeploymentCreate } from '@/client/deploymentClient.ts'

import type { BaseRefModel } from '@/model/BaseModel.ts'
import type { DeploymentNewModel } from '@/model/DeploymentModel.ts'

const model: Ref<DeploymentNewModel> = ref({
  id: 0,
  version: 0,
  projectRef: null,
  projectSiteRef: null,
  name: null,
  description: null,
})

const projects: Ref<BaseRefModel[]> = ref([])
const sites: Ref<BaseRefModel[]> = ref([])

const loading = ref(false)

const create = () => {
  loading.value = true
  invokeDeploymentCreate(model.value)
    .then((data) => router.push(`/deployments/${data.id}`))
    .catch(() => {})
    .finally(() => (loading.value = false))
}

const projectItems = (q: string) => {
  invokeProjectItemListGet(q)
    .then((data) => (projects.value = data))
    .catch(() => {})
}

const siteItems = (projectId: number | null, q: string | null) => {
  if (!projectId) {
    return
  }
  invokeSiteItemListGet(projectId, q)
    .then((data) => (sites.value = data))
    .catch(() => {})
}
</script>

<template>
  <div class="mt-5">
    <h1 class="text-xl">Create New Deployment</h1>
    <div class="flex flex-col gap-5 my-4">
      <FloatLabel
        variant="on"
        class="w-full"
      >
        <InputText
          id="name"
          class="w-full"
          v-model="model.name"
        />
        <label for="name">Name</label>
      </FloatLabel>

      <FloatLabel
        variant="on"
        class="w-full"
      >
        <Textarea
          class="w-full"
          v-model="model.description"
          placeholder1="Description"
        />
        <label for="1name">Description</label>
      </FloatLabel>

      <FloatLabel
        variant="on"
        class="w-full"
      >
        <AutoComplete
          v-model="model.projectRef"
          dropdown
          :suggestions="projects"
          option-label="name"
          force-selection
          @change="model.projectSiteRef = null"
          @complete="projectItems($event.query)"
        />
        <label for="1name">Project</label>
      </FloatLabel>

      <FloatLabel
        variant="on"
        class="w-full"
      >
        <AutoComplete
          v-model="model.projectSiteRef"
          dropdown
          :suggestions="sites"
          option-label="name"
          force-selection
          @complete="siteItems(model.projectRef?.id ?? null, $event.query)"
        />
        <label for="1name">Site</label>
      </FloatLabel>
    </div>

    <Button
      label="Create"
      icon="pi pi-plus"
      :loading="loading"
      @click="create()"
    />
  </div>
</template>
