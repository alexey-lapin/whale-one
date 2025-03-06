<script setup lang="ts">
import { type Ref, ref } from 'vue'
import router from '@/router'
import { invokeUserCreate } from '@/client/userClient.ts'

import type { UserNewModel } from '@/model/UserModel.ts'
import Password from 'primevue/password'
import InputText from 'primevue/inputtext'
import FloatLabel from 'primevue/floatlabel'
import Button from 'primevue/button'

const model: Ref<UserNewModel> = ref({
  id: 0,
  version: 0,
  createdAt: '',
  createdBy: {
    id: 0,
    name: '',
  },
  username: '',
  password: '',
  enabled: true,
  authorities: [],
})

const loading = ref(false)

const create = () => {
  loading.value = true
  invokeUserCreate(model.value)
    .then((data) => {
      return router.push(`/administration/users/${data.id}`)
    })
    .catch(() => {})
    .finally(() => {
      loading.value = false
    })
}
</script>

<template>
  <div class="mt-5">
    <h1 class="text-xl">Create New User</h1>
    <div class="flex flex-col gap-5 my-4">
      <FloatLabel variant="on">
        <InputText
          id="name"
          v-model="model.username"
        />
        <label for="name">Username</label>
      </FloatLabel>

      <FloatLabel variant="on">
        <Password
          v-model="model.password"
          toogle-mask
        />
        <label for="1name">Password</label>
      </FloatLabel>
    </div>
    <Button
      label="Create"
      icon="pi pi-plus"
      :loading="loading"
      @click="create()"
    />
  </div>
</template>
