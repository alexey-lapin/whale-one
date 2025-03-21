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
  return idVisible.value ? `#${model.value.id} ${base}` : base
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
  <Card class="border hover:border-surface-500">
    <template #subtitle>
      <div class="flex items-center">
        <span
          class="flex-grow"
          @click="idVisible = !idVisible"
          >{{ header }}</span
        >
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
    </template>
    <template #content>
      <Fluid>
        <div class="flex flex-col gap-4">
          <FloatLabel variant="on">
            <InputText
              id="name"
              v-model="model.name"
              :disabled="!editableState"
            />
            <label for="name">Name</label>
          </FloatLabel>
          <!--          <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">-->
          <!--            <FloatLabel variant="on">-->
          <!--              <InputNumber-->
          <!--                id="longitude"-->
          <!--                v-model="model.longitude"-->
          <!--                :disabled="!editableState"-->
          <!--              />-->
          <!--              <label for="longitude">Longitude</label>-->
          <!--            </FloatLabel>-->
          <!--            <FloatLabel variant="on">-->
          <!--              <InputNumber-->
          <!--                id="latitude"-->
          <!--                v-model="model.latitude"-->
          <!--                :disabled="!editableState"-->
          <!--              />-->
          <!--              <label for="latitude">Latitude</label>-->
          <!--            </FloatLabel>-->
          <!--            <FloatLabel variant="on">-->
          <!--              <InputNumber-->
          <!--                id="depth"-->
          <!--                v-model="model.depth"-->
          <!--                :disabled="!editableState"-->
          <!--              />-->
          <!--              <label for="depth">Depth</label>-->
          <!--            </FloatLabel>-->
          <!--          </div>-->
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
