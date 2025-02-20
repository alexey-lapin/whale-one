<script setup lang="ts">
import dayjs from 'dayjs'

import InputNumber from 'primevue/inputnumber'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import Panel from 'primevue/panel'
import InputText from 'primevue/inputtext'

import type { EntityHeaderModel } from '@/model/BaseModel.ts'

defineProps<{
  header: string
  model: EntityHeaderModel
}>()
</script>

<template>
  <Panel
    :header="header"
    toggleable
    collapsed
  >
    <Fluid>
      <div class="mt-1 grid sm:grid-cols-3 grid-cols-1 gap-3">
        <FloatLabel variant="on">
          <InputNumber
            id="id"
            v-model="model.id"
            disabled
          />
          <label for="id">Id</label>
        </FloatLabel>
        <FloatLabel
          variant="on"
          class="col1-span-3"
        >
          <InputText
            id="version"
            :model-value="dayjs(model.createdAt).format('YYYY-MM-DD HH:mm:ss ZZ')"
            disabled
          />
          <label for="version">Created At</label>
        </FloatLabel>
        <FloatLabel
          variant="on"
          class="col1-span-2"
        >
          <InputText
            id="version"
            v-model="model.createdBy.name"
            disabled
          />
          <label for="version">Created By</label>
        </FloatLabel>
        <FloatLabel variant="on">
          <InputNumber
            id="version"
            v-model="model.version"
            disabled
          />
          <label for="version">Version</label>
        </FloatLabel>
        <FloatLabel v-if="model.lastUpdatedAt"
          variant="on"
          class="col1-span-3"
        >
          <InputText
            id="version"
            :model-value="dayjs(model.lastUpdatedAt).format('YYYY-MM-DD HH:mm:ss ZZ')"
            disabled
          />
          <label for="version">Last Updated At</label>
        </FloatLabel>
        <FloatLabel v-if="model.lastUpdatedBy"
          variant="on"
          class="col1-span-2"
        >
          <InputText
            id="version"
            v-model="model.lastUpdatedBy.name"
            disabled
          />
          <label for="version">Last Updated By</label>
        </FloatLabel>
      </div>
    </Fluid>
  </Panel>
</template>

