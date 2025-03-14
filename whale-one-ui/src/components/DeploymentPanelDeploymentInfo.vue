<script setup lang="ts">
import { computed, ref, type Ref } from 'vue'

import AutoComplete from 'primevue/autocomplete'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import DatePicker from 'primevue/datepicker'
import FloatLabel from 'primevue/floatlabel'
import InputNumber from 'primevue/inputnumber'

import { invokeCampaignListGet } from '@/client/projectClient.ts'

import type { DeploymentModel } from '@/model/DeploymentModel.ts'
import type { ProjectCampaignModel } from '@/model/ProjectModel.ts'

const model = defineModel<DeploymentModel>({ required: true })
defineProps<{
  editing: boolean
}>()
const emits = defineEmits(['save-clicked'])

const campaigns: Ref<ProjectCampaignModel[]> = ref([])

const getCampaigns = () => {
  invokeCampaignListGet(model.value.id)
    .then((data) => (campaigns.value = data))
    .catch(() => {})
}

const deployedAt = computed({
  get: () => (model.value.deployedAt ? new Date(model.value.deployedAt) : null),
  set: (newValue) => (model.value.deployedAt = newValue ? new Date(newValue).toISOString() : null),
})

const firstFileAt = computed({
  get: () => (model.value.firstFileAt ? new Date(model.value.firstFileAt) : null),
  set: (newValue) => (model.value.firstFileAt = newValue ? new Date(newValue).toISOString() : null),
})

const lastFileAt = computed({
  get: () => (model.value.lastFileAt ? new Date(model.value.lastFileAt) : null),
  set: (newValue) => (model.value.lastFileAt = newValue ? new Date(newValue).toISOString() : null),
})
</script>

<template>
  <div class="flex flex-wrap gap-2">
    <FloatLabel variant="on">
      <InputNumber
        id="latitude"
        v-model="model.latitude"
        :minFractionDigits="6"
        :maxFractionDigits="6"
        :use-grouping="false"
        mode="decimal"
        :min="-90"
        :max="90"
        :disabled="!editing"
      />
      <label for="name">Latitude</label>
    </FloatLabel>
    <FloatLabel variant="on">
      <InputNumber
        id="longitude"
        v-model="model.longitude"
        :minFractionDigits="6"
        :maxFractionDigits="6"
        :use-grouping="false"
        mode="decimal"
        :min="-180"
        :max="180"
        :disabled="!editing"
      />
      <label for="name">Longitude</label>
    </FloatLabel>
    <FloatLabel variant="on">
      <InputNumber
        id="bathymetry"
        v-model="model.bathymetry"
        :minFractionDigits="0"
        :maxFractionDigits="2"
        :use-grouping="false"
        mode="decimal"
        :disabled="!editing"
      />
      <label for="name">Bathymetry Depth, m</label>
    </FloatLabel>
  </div>
  <div class="mt-2 flex flex-wrap gap-2">
    <FloatLabel variant="on">
      <DatePicker
        v-model="deployedAt"
        date-format="yy-mm-dd"
        show-time
        hour-format="24"
        :disabled="!editing"
      />
      <label for="name">Deployment Date</label>
    </FloatLabel>
    <FloatLabel variant="on">
      <InputText :disabled="!editing" />
      <label for="name">Deployment Vessel</label>
    </FloatLabel>
    <FloatLabel variant="on">
      <AutoComplete
        v-if="editing"
        v-model="model.deploymentCampaignRef"
        dropdown
        :suggestions="campaigns"
        option-label="name"
        force-selection
        @complete="getCampaigns()"
      />
      <InputText
        v-else
        :model-value="model.deploymentCampaignRef?.name"
        disabled
      />
      <label for="name">Campaign</label>
    </FloatLabel>
  </div>
  <div class="mt-2 flex flex-wrap gap-2">
    <FloatLabel variant="on">
      <DatePicker
        v-model="firstFileAt"
        date-format="yy-mm-dd"
        show-time
        hour-format="24"
        :disabled="!editing"
      />
      <label for="name">Time of the first file (in water)</label>
    </FloatLabel>
    <FloatLabel variant="on">
      <DatePicker
        v-model="lastFileAt"
        date-format="yy-mm-dd"
        show-time
        hour-format="24"
        :disabled="!editing"
      />
      <label for="name">Time of the last file (in water)</label>
    </FloatLabel>
  </div>
  <Button
    v-if="editing"
    label="Save"
    icon="pi pi-save"
    class="mt-3"
    @click="emits('save-clicked')"
  />
</template>
