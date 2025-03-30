<script setup lang="ts">
import { computed, ref, type Ref } from 'vue'

import AutoComplete from 'primevue/autocomplete'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import DatePicker from 'primevue/datepicker'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import Select from 'primevue/select'

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
  invokeCampaignListGet(model.value.projectRef.id)
    .then((data) => (campaigns.value = data))
    .catch(() => {})
}

const recoveredAt = computed({
  get: () => (model.value.recoveredAt ? new Date(model.value.recoveredAt) : null),
  set: (newValue) => (model.value.recoveredAt = newValue ? new Date(newValue).toISOString() : null),
})
</script>

<template>
  <Fluid>
    <div class="grid grid-cols-3 gap-2">
      <FloatLabel variant="on">
        <Select
          v-model="model.recoveryStatus"
          :options="['OK', 'Lost', 'Damaged']"
          class="w-60"
          :disabled="!editing"
        />
        <label for="name">Recovery Status</label>
      </FloatLabel>
    </div>
    <div class="mt-2 grid grid-cols-3 gap-2">
      <FloatLabel variant="on">
        <DatePicker
          v-model="recoveredAt"
          date-format="yy-mm-dd"
          show-time
          hour-format="24"
          :disabled="!editing"
        />
        <label for="name">Recovery Date</label>
      </FloatLabel>
      <FloatLabel variant="on">
        <InputText :disabled="!editing" />
        <label for="name">Recovery Vessel</label>
      </FloatLabel>
      <FloatLabel variant="on">
        <AutoComplete
          v-if="editing"
          v-model="model.recoveryCampaignRef"
          dropdown
          :suggestions="campaigns"
          option-label="name"
          force-selection
          @complete="getCampaigns()"
        />
        <InputText
          v-else
          :model-value="model.recoveryCampaignRef?.name"
          disabled
        />
        <label for="name">Campaign</label>
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
