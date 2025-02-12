<script setup lang="ts">
import { onMounted, type Ref, ref } from 'vue'

import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import Panel from 'primevue/panel'
import Textarea from 'primevue/textarea'

import { useToast } from 'primevue/usetoast'
import type ProjectSiteModel from '@/model/ProjectSiteModel.ts'
import ProjectSite from '@/components/ProjectSite.vue'
import dayjs from 'dayjs'

import { errorToast, successToast } from '@/utils/toasts'

const toast = useToast()

const props = defineProps<{
  id: number
}>()

const project = ref({
  id: 0,
  version: 0,
  createdAt: null,
  createdBy: null,
  name: null,
  description: null
})

const sites: Ref<ProjectSiteModel[]> = ref([])
const newSite: Ref<ProjectSiteModel> = ref({
  id: 0,
  projectId: props.id,
  name: '',
  longitude: null,
  latitude: null,
  depth: null
})

const loading = ref(false)
const addingNewSite = ref(false)

const getProject = () => {
  return fetch(`/api/projects/${props.id}`)
    .then(response => response.json())
    .then(data => project.value = data)
    .catch(error => console.error(error))
}

const getSites = () => {
  return fetch(`/api/projects/${props.id}/sites`)
    .then(response => response.json())
    .then(data => sites.value = data)
    .catch(error => console.error(error))
}

const updateProject = () => {
  loading.value = true
  fetch(`/api/projects/${props.id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(project.value)
  })
    .then(response => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error('Failed to update project')
      }
    })
    .then(data => {
      project.value = data
      toast.add(successToast(`Project #${data.id} ${data.name} updated`))
    })
    .catch(error => {
      toast.add(errorToast(error.message))
      console.error(error)
    })
    .finally(() => {
      loading.value = false
    })
}

const onSiteUpdated = () => {
  getSites()
  addingNewSite.value = false
}

const onSiteDeleted = () => {
  addingNewSite.value = false
}

onMounted(() => {
  getProject()
  getSites()
})
</script>

<template>
  <div class="mt-5">
    <h1 class="text-xl">Project</h1>
    <div class="flex flex-col gap-5 my-4">
      <Panel header="Id" toggleable collapsed>
        <Fluid>
          <div class="mt-1 grid grid-cols-6 gap-3 xs:grid-cols-1">
            <FloatLabel variant="on">
              <InputNumber id="id" v-model="project.id" disabled />
              <label for="id">Id</label>
            </FloatLabel>
            <FloatLabel variant="on">
              <InputNumber id="version" v-model="project.version" disabled />
              <label for="version">Version</label>
            </FloatLabel>
            <FloatLabel variant="on" class="col-span-2">
              <InputText id="version"
                         :model-value="dayjs(project.createdAt).format('YYYY-MM-DD HH:mm:ss ZZ')"
                         disabled />
              <label for="version">Created At</label>
            </FloatLabel>
            <FloatLabel variant="on" class="col-span-2">
              <InputText id="version" v-model="project.createdBy" disabled />
              <label for="version">Created By</label>
            </FloatLabel>
          </div>
        </Fluid>
      </Panel>

      <Panel header="Attributes" toggleable>
        <div class="mt-1 flex flex-col gap-3">
          <FloatLabel variant="on" class="w-full">
            <InputText id="name" class="w-full" v-model="project.name" />
            <label for="name">Name</label>
          </FloatLabel>

          <FloatLabel variant="on" class="w-full">
            <Textarea class="w-full" v-model="project.description" />
            <label for="1name">Description</label>
          </FloatLabel>
        </div>
        <Button label="Save" icon="pi pi-save" class="mt-4" :loading="loading"
                @click="updateProject()" />
      </Panel>

      <Panel header="Sites" toggleable>
        <div class="mt-1 flex flex-col gap-3">
          <ProjectSite v-for="(site, index) in sites"
                       :key="site.id"
                       :modelValue="site"
                       @site-deleted="getSites()" />


          <ProjectSite v-if="addingNewSite"
                       v-model="newSite"
                       :editable="true"
                       @site-updated="onSiteUpdated()"
                       @site-deleted="onSiteDeleted()" />
        </div>
        <Button v-if="!addingNewSite"
                class="mt-3"
                label="New"
                severity="secondary"
                icon="pi pi-plus"
                @click="addingNewSite = true" />
      </Panel>
    </div>
  </div>
</template>
