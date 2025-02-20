<script setup lang="ts">
import { ref } from 'vue'

import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'

import router from '@/router'
import { invokeProjectCreate } from '@/client/projectClient.ts'

const model = ref({
  id: 0,
  version: 0,
  createdAt: '',
  createdBy: {
    id: 0,
    name: '',
  },
  lastUpdatedAt: '',
  lastUpdatedBy: {
    id: 0,
    name: '',
  },
  name: '',
  client: null,
  ownership: null,
  region: null,
  type: null,
  goal: null,
  description: null,
})

const loading = ref(false)

const create = () => {
  loading.value = true
  invokeProjectCreate(model.value)
    .then((data) => router.push(`/projects/${data.id}`))
    .catch(() => {})
    .finally(() => (loading.value = false))
}
</script>

<template>
  <div class="mt-5">
    <h1 class="text-xl">Create New Project</h1>
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
          placeholder1="Description"
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
