<script setup lang="ts">
import { onMounted, type Ref, ref } from 'vue'

import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import InputText from 'primevue/inputtext'
import Panel from 'primevue/panel'
import Textarea from 'primevue/textarea'

import EntityHeader from '@/components/EntityHeader.vue'

import type { AnalysisTypeModel } from '@/model/AnalysisTypeModel.ts'
import { invokeAnalysisTypeGet, invokeAnalysisTypeUpdate } from '@/client/analysisTypeClient.ts'
import AnalysisTypeAttribute from '@/components/AnalysisTypeAttribute.vue'

const props = defineProps<{
  id: number
}>()

const model: Ref<AnalysisTypeModel> = ref({
  id: 0,
  version: 0,
  createdAt: '',
  createdBy: {
    id: 0,
    name: '',
  },
  name: '',
})

const loading = ref(false)
const editingInfo = ref(false)
const editingAttributes = ref(false)

const getAnalysisType = () => {
  return invokeAnalysisTypeGet(props.id)
    .then((data) => (model.value = data))
    .catch(() => {})
}

const updateAnalysisType = () => {
  if (editingAttributes.value) {
    // updateManufacturersInModel()
  }
  loading.value = true
  invokeAnalysisTypeUpdate(model.value)
    .then((data) => {
      model.value = data
      editingInfo.value = false
      editingAttributes.value = false
    })
    .catch(() => {})
    .finally(() => {
      loading.value = false
    })
}

const addAttribute = () => {
  model.value.metadata?.attributes.push({
    name: '',
    type: '',
    metadata: {},
  })
}

const deleteAttribute = (index: number) => {
  model.value.metadata?.attributes.splice(index, 1)
}

onMounted(() => {
  getAnalysisType().then(() => {
    if (!model.value.metadata) {
      model.value.metadata = {
        attributes: [],
      }
    }
    if (!model.value.metadata.attributes) {
      model.value.metadata.attributes = []
    }
  })
})
</script>

<template>
  <div class="mt-5">
    <div class="flex flex-col gap-5 my-4">
      <EntityHeader
        header="Analysis Type"
        :model="model"
      />

      <Panel header="Info">
        <template #icons>
          <div class="flex gap-2">
            <Button
              v-if="!editingInfo"
              severity="secondary"
              size="small"
              icon="pi pi-pencil"
              @click="editingInfo = !editingInfo"
            />
          </div>
        </template>
        <template #default>
          <div class="mt-1 flex flex-col gap-3">
            <FloatLabel
              variant="on"
              class="w-full"
            >
              <InputText
                id="name"
                class="w-full"
                v-model="model.name"
                :disabled="!editingInfo"
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
                :disabled="!editingInfo"
              />
              <label for="1name">Description</label>
            </FloatLabel>
          </div>
          <Button
            v-if="editingInfo"
            label="Save"
            icon="pi pi-save"
            class="mt-4"
            :loading="loading"
            @click="updateAnalysisType()"
          />
        </template>
      </Panel>

      <Panel header="Attributes">
        <template #icons>
          <div class="flex gap-2">
            <Button
              severity="secondary"
              size="small"
              icon="pi pi-pencil"
              @click="editingAttributes = !editingAttributes"
            />
          </div>
        </template>
        <template #default>
          <template v-if="model.metadata && model.metadata.attributes">
            <div class="flex flex-col gap-3">
              <template v-for="(attribute, index) in model.metadata.attributes">
                <AnalysisTypeAttribute
                  v-model="model.metadata.attributes[index]"
                  :editable="editingAttributes"
                  @attribute-deleted="deleteAttribute(index)"
                />
              </template>
            </div>
          </template>
          <div class="flex gap-2">
            <Button
              v-if="editingAttributes"
              label="Save"
              icon="pi pi-save"
              class="mt-3"
              @click="updateAnalysisType()"
            />
            <Button
              v-if="editingAttributes"
              label="New"
              icon="pi pi-plus"
              severity="secondary"
              class="mt-3"
              @click="addAttribute()"
            />
          </div>
        </template>
      </Panel>
    </div>
  </div>
</template>
