<script setup lang="ts">
import dayjs from 'dayjs'
import FloatLabel from 'primevue/floatlabel'
import Fluid from 'primevue/fluid'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'

import type { EntityHeaderModel } from '@/model/BaseModel.ts'

defineProps<{
  model: EntityHeaderModel
}>()
</script>

<template>
  <Fluid>
    <div class="mt-1 grid sm:grid-cols-3 grid-cols-1 gap-3">
      <FloatLabel variant="on">
        <InputNumber
          v-model="model.id"
          disabled
        />
        <label>Id</label>
      </FloatLabel>
      <FloatLabel
        variant="on"
        class="col1-span-3"
      >
        <InputText
          :model-value="dayjs(model.createdAt).format('YYYY-MM-DD HH:mm:ss ZZ')"
          disabled
        />
        <label>Created At</label>
      </FloatLabel>
      <FloatLabel
        variant="on"
        class="col1-span-2"
      >
        <InputText
          v-model="model.createdBy.name"
          disabled
        />
        <label>Created By</label>
      </FloatLabel>
      <FloatLabel variant="on">
        <InputNumber
          v-model="model.version"
          disabled
        />
        <label>Version</label>
      </FloatLabel>
      <FloatLabel
        v-if="model.lastUpdatedAt"
        variant="on"
        class="col1-span-3"
      >
        <InputText
          :model-value="dayjs(model.lastUpdatedAt).format('YYYY-MM-DD HH:mm:ss ZZ')"
          disabled
        />
        <label>Last Updated At</label>
      </FloatLabel>
      <FloatLabel
        v-if="model.lastUpdatedBy"
        variant="on"
        class="col1-span-2"
      >
        <InputText
          v-model="model.lastUpdatedBy.name"
          disabled
        />
        <label>Last Updated By</label>
      </FloatLabel>
    </div>
  </Fluid>
</template>
