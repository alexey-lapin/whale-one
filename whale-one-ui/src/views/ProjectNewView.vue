<script setup lang="ts">
import { ref } from 'vue'

import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
import { useToast } from 'primevue/usetoast'
import router from '@/router'

const toast = useToast()

const model = ref({
  id: 0,
  version: 0,
  name: null,
  description: null
})

const loading = ref(false)

const create = () => {
  loading.value = true
  fetch('/api/projects', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(model.value)
  })
    .then(response => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error('Failed to create project')
      }
    })
    .then(data => {
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: `Project #${data.id} ${data.name} created`,
        life: 3000
      })
      return router.push(`/projects/${data.id}`)
    })
    .catch(error => {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: error.message,
        life: 3000
      })
      console.error(error)
    })
    .finally(() => {
      loading.value = false
    })
}
</script>

<template>
  <div class="mt-5">
    <h1 class="text-xl">Create New Project</h1>
    <div class="flex flex-col gap-5 my-4">
      <FloatLabel variant="on" class="w-full">
        <InputText id="name" class="w-full" v-model="model.name" />
        <label for="name">Name</label>
      </FloatLabel>

      <FloatLabel variant="on" class="w-full">
        <Textarea class="w-full" v-model="model.description" placeholder1="Description" />
        <label for="1name">Description</label>
      </FloatLabel>
    </div>
    <Button label="Create" icon="pi pi-plus" :loading="loading" @click="create()"></Button>
  </div>
</template>
