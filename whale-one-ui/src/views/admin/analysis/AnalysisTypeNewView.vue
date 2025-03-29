<script setup lang="ts">
import { ref } from 'vue'

import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'

import router from '@/router'
import { invokeAnalysisTypeCreate } from '@/client/analysisTypeClient.ts'

const model = ref({
  id: 0,
  version: 0,
  createdAt: '',
  createdBy: {
    id: 0,
    name: '',
  },
  name: '',
  description: null,
})

const loading = ref(false)

const create = () => {
  loading.value = true
  invokeAnalysisTypeCreate(model.value)
    .then((data) => {
      return router.push(`/administration/analysis/types/${data.id}`)
    })
    .catch(() => {})
    .finally(() => {
      loading.value = false
    })
}
</script>

<template>
  <div class="mt-5">
    <h1 class="text-xl">Create New Analysis Type</h1>
    <div class="flex flex-col gap-5 my-4">
      <FloatLabel
        variant="on"
        class="w-full"
      >
        <InputText
          id="name"
          class="w-full"
          v-model="model.name"
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
        />
        <label for="1name">Description</label>
      </FloatLabel>
    </div>
    <Button
      label="Create"
      icon="pi pi-plus"
      :loading="loading"
      @click="create()"
    ></Button>
  </div>
</template>
