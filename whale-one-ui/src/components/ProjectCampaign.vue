<script setup lang="ts">
import { computed, ref } from 'vue'

import Button from 'primevue/button'
import Card from 'primevue/card'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import InputText from 'primevue/inputtext'
import { useConfirm } from 'primevue/useconfirm'

import type { ProjectCampaignModel } from '@/model/ProjectModel.ts'
import { invokeCampaignCreateOrUpdate, invokeCampaignDelete } from '@/client/projectClient.ts'
import { deleteConfirm } from '@/utils/confirms'

const confirm = useConfirm()

const model = defineModel<ProjectCampaignModel>({ required: true })
const { editable = false } = defineProps<{
  editable?: boolean
}>()
const emits = defineEmits(['campaign-updated', 'campaign-deleted'])

const editableState = ref(editable)
const idVisible = ref(false)

const header = computed(() => {
  const base = 'Campaign'
  if (model.value.id < 1) {
    return `New ${base}`
  }
  return idVisible.value ? `${base} #${model.value.id} ` : base
})

const createOrUpdateCampaign = () => {
  invokeCampaignCreateOrUpdate(model.value)
    .then((data) => {
      editableState.value = false
      emits('campaign-updated', data)
    })
    .catch(() => {})
}

const deleteCampaign = () => {
  invokeCampaignDelete(model.value)
    .then(() => {
      emits('campaign-deleted', model.value.id)
    })
    .catch(() => {})
}

const confirmDelete = () => {
  if (model.value.id < 1) {
    emits('campaign-deleted', model.value.id)
    return
  }
  confirm.require(
    deleteConfirm(`Delete campaign #${model.value.id} ${model.value.name}?`, () =>
      deleteCampaign(),
    ),
  )
}
</script>

<template>
  <Card class="border hover:border-surface-500 dark:border-surface-700">
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
              v-model="model.name"
              :disabled="!editableState"
            />
            <label>Name</label>
          </FloatLabel>

          <FloatLabel variant="on">
            <InputText
              v-model="model.vessel"
              :disabled="!editableState"
            />
            <label>Vessel</label>
          </FloatLabel>
        </div>
      </Fluid>
      <Button
        v-if="editableState"
        label="Save"
        icon="pi pi-save"
        class="mt-4"
        @click="createOrUpdateCampaign()"
      />
    </template>
  </Card>
</template>
