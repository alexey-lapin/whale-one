<script setup lang="ts">
import AutoComplete from 'primevue/autocomplete'
import Button from 'primevue/button'
import InputGroup from 'primevue/inputgroup'
import InputGroupAddon from 'primevue/inputgroupaddon'
import InputText from 'primevue/inputtext'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import type { DeploymentModel } from '@/model/DeploymentModel.ts'
import { ref, type Ref } from 'vue'
import type { BaseRefModel } from '@/model/BaseModel.ts'
import { invokeProjectItemListGet, invokeSiteItemListGet } from '@/client/projectClient.ts'
import router from '@/router'

const model = defineModel<DeploymentModel>({ required: true })
defineProps<{
  editing: boolean
}>()
const emits = defineEmits(['save-clicked'])

const projects: Ref<BaseRefModel[]> = ref([])
const sites: Ref<BaseRefModel[]> = ref([])

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
  <Fluid>
    <div class="flex flex-col gap-3">
      <FloatLabel variant="on">
        <InputText
          v-model="model.name"
          :disabled="!editing"
        />
        <label for="name">Name</label>
      </FloatLabel>
      <FloatLabel variant="on">
        <InputText
          v-model="model.description"
          :disabled="!editing"
        />
        <label for="description">Description</label>
      </FloatLabel>
      <FloatLabel variant="on">
        <AutoComplete
          v-if="editing"
          v-model="model.projectRef"
          dropdown
          :suggestions="projects"
          option-label="name"
          force-selection
          @change="model.projectSiteRef = { id: -1, name: '' }"
          @complete="projectItems($event.query)"
        />
        <InputGroup v-else>
          <InputText
            v-model="model.projectRef.name"
            disabled
          />
          <InputGroupAddon>
            <Button
              icon="pi pi-external-link"
              severity="secondary"
              variant="text"
              @click="router.push(`/projects/${model.projectRef.id}`)"
            />
          </InputGroupAddon>
        </InputGroup>
        <label for="description">Project</label>
      </FloatLabel>
      <FloatLabel variant="on">
        <AutoComplete
          v-if="editing"
          v-model="model.projectSiteRef"
          dropdown
          :suggestions="sites"
          option-label="name"
          force-selection
          @complete="siteItems(model.projectRef?.id ?? null, $event.query)"
        />
        <InputText
          v-else
          v-model="model.projectSiteRef.name"
          disabled
        />
        <label for="description">Site</label>
      </FloatLabel>
      <FloatLabel variant="on">
        <InputText
          v-model="model.platform"
          :disabled="!editing"
        />
        <label for="description">Platform</label>
      </FloatLabel>
      <FloatLabel variant="on">
        <InputText
          v-model="model.providerOrganisations"
          :disabled="!editing"
        />
        <label for="description">Provider Organisations</label>
      </FloatLabel>
      <FloatLabel variant="on">
        <InputText
          v-model="model.providerParticipants"
          :disabled="!editing"
        />
        <label for="description">Provider Participants</label>
      </FloatLabel>
    </div>
  </Fluid>
  <Button
    v-if="editing"
    label="Save"
    icon="pi pi-save"
    class="mt-3"
    @click="emits('save-clicked')"
  />
</template>
