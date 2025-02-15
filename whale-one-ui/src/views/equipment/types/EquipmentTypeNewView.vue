<script setup lang="ts">
import { ref } from 'vue'

import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
import { useToast } from 'primevue/usetoast'

import { errorToast, successToast } from '@/utils/toasts.ts'
import router from '@/router'

const toast = useToast()

const model = ref({
  id: 0,
  version: 0,
  name: null,
  description: null,
})

const loading = ref(false)

const create = () => {
  loading.value = true
  fetch('/api/equipment/types', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(model.value),
  })
    .then((response) => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error('Failed to create equipment type')
      }
    })
    .then((data) => {
      toast.add(successToast(`Equipment Type #${data.id} ${data.name} created`))
      return router.push(`/equipment/types/${data.id}`)
    })
    .catch((error) => {
      toast.add(errorToast(error.message))
      console.error(error)
    })
    .finally(() => {
      loading.value = false
    })
}
</script>

<template>
  <div class="mt-5">
    <h1 class="text-xl">Create New Equipment Type</h1>
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
