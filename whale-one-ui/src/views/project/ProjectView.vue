<script setup lang="ts">
import { onMounted, type Ref, ref } from 'vue'

import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import InputText from 'primevue/inputtext'
import Panel from 'primevue/panel'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'

import ProjectSite from '@/components/ProjectSite.vue'
import {
  invokeCampaignListGet,
  invokeProjectGet,
  invokeProjectUpdate,
  invokeSiteListGet,
} from '@/client/projectClient.ts'
import ProjectCampaign from '@/components/ProjectCampaign.vue'
import EntityHeaderDialog from '@/components/EntityHeaderDialog.vue'

import type { ProjectCampaignModel, ProjectModel, ProjectSiteModel } from '@/model/ProjectModel.ts'

const props = defineProps<{
  id: number
}>()

const model: Ref<ProjectModel | null> = ref(null)

const sites: Ref<ProjectSiteModel[]> = ref([])
const newSite: Ref<ProjectSiteModel> = ref({
  id: 0,
  projectId: props.id,
  name: '',
  longitude: null,
  latitude: null,
  depth: null,
})

const campaigns: Ref<ProjectCampaignModel[]> = ref([])
const newCampaign: Ref<ProjectCampaignModel> = ref({
  id: 0,
  projectId: props.id,
  name: '',
})

const loading = ref(false)
const editing = ref(false)
const addingNewSite = ref(false)
const addingNewCampaign = ref(false)

const getProject = () => {
  invokeProjectGet(props.id)
    .then((data) => (model.value = data))
    .catch(() => {})
}

const getSites = () => {
  invokeSiteListGet(props.id)
    .then((data) => (sites.value = data))
    .catch(() => {})
}

const getCampaigns = () => {
  invokeCampaignListGet(props.id)
    .then((data) => (campaigns.value = data))
    .catch(() => {})
}

const updateProject = () => {
  if (model.value === null) {
    return
  }
  loading.value = true
  invokeProjectUpdate(model.value)
    .then((data) => {
      editing.value = false
      return (model.value = data)
    })
    .catch(() => {})
    .finally(() => (loading.value = false))
}

const onSiteUpdated = () => {
  getSites()
  addingNewSite.value = false
}

const onSiteDeleted = () => {
  addingNewSite.value = false
}

const onCampaignUpdated = () => {
  getCampaigns()
  addingNewCampaign.value = false
}

const oncampaignDeleted = () => {
  addingNewCampaign.value = false
}

onMounted(() => {
  getProject()
  getSites()
  getCampaigns()
})
</script>

<template>
  <div
    v-if="model"
    class="my-4 flex flex-col gap-4"
  >
    <Panel>
      <template #header>
        <EntityHeaderDialog
          :model="model"
          v-slot="{ toggle }"
        >
          <span
            class="p-panel-title cursor-pointer"
            @click="toggle()"
          >
            Project
          </span>
        </EntityHeaderDialog>
      </template>
      <template #icons>
        <Button
          icon="pi pi-pencil"
          severity="secondary"
          size="small"
          variant="text"
          @click="editing = !editing"
        >
        </Button>
      </template>
      <template #default>
        <Fluid>
          <div class="mt-1 flex flex-col gap-3">
            <FloatLabel variant="on">
              <InputText
                id="name"
                v-model="model.name"
                :disabled="!editing"
              />
              <label for="name">Name</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputText
                id="name"
                v-model="model.client"
                :disabled="!editing"
              />
              <label for="name">Client</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputText
                id="name"
                v-model="model.ownership"
                :disabled="!editing"
              />
              <label for="name">Ownership</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputText
                id="name"
                v-model="model.region"
                :disabled="!editing"
              />
              <label for="name">Region</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <Select
                id="name"
                v-model="model.type"
                :options="['research', 'consultancy']"
                editable
                :disabled="!editing"
              />
              <label for="name">Type</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <InputText
                id="name"
                v-model="model.goal"
                :disabled="!editing"
              />
              <label for="name">Goal</label>
            </FloatLabel>

            <FloatLabel variant="on">
              <Textarea
                v-model="model.description"
                :disabled="!editing"
              />
              <label for="1name">Description</label>
            </FloatLabel>
          </div>
        </Fluid>
        <Button
          v-if="editing"
          label="Save"
          icon="pi pi-save"
          class="mt-4"
          :loading="loading"
          @click="updateProject()"
        />
      </template>
    </Panel>

    <Panel
      header="Sites"
      toggleable
    >
      <div class="mt-1 flex flex-col gap-3">
        <ProjectSite
          v-for="(site, index) in sites"
          :key="site.id"
          :modelValue="site"
          @site-deleted="getSites()"
        />

        <ProjectSite
          v-if="addingNewSite"
          v-model="newSite"
          :editable="true"
          @site-updated="onSiteUpdated()"
          @site-deleted="onSiteDeleted()"
        />
      </div>
      <Button
        v-if="!addingNewSite"
        class="mt-3"
        label="New"
        severity="secondary"
        icon="pi pi-plus"
        @click="addingNewSite = true"
      />
    </Panel>

    <Panel
      header="Campaigns"
      toggleable
    >
      <div class="mt-1 flex flex-col gap-3">
        <ProjectCampaign
          v-for="(campaign, index) in campaigns"
          :key="campaign.id"
          :modelValue="campaign"
          @campaign-deleted="getCampaigns()"
        />

        <ProjectCampaign
          v-if="addingNewCampaign"
          v-model="newCampaign"
          :editable="true"
          @campaign-updated="onCampaignUpdated()"
          @campaign-deleted="oncampaignDeleted()"
        />
      </div>
      <Button
        v-if="!addingNewCampaign"
        class="mt-3"
        label="New"
        severity="secondary"
        icon="pi pi-plus"
        @click="addingNewCampaign = true"
      />
    </Panel>
  </div>
</template>
