<script setup lang="ts">
import { ref } from 'vue'

import Button from 'primevue/button'
import Card from 'primevue/card'
import Fluid from 'primevue/fluid'
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'

import { useAuthStore } from '@/stores/auth.ts'

const props = defineProps<{
  redirect?: string
}>()

const auth = useAuthStore()

const model = ref({
  username: '',
  password: '',
})

const error = ref(false)

const login = () => {
  auth
    .login(model.value.username, model.value.password, props.redirect)
    .then(() => (error.value = false))
    .catch(() => (error.value = true))
}
</script>

<template>
  <div class="flex items-center justify-center h-screen">
    <Card class="w-96 p-6">
      <template #title>
        <h2 class="text-center text-6xl mb-4">🐳</h2>
      </template>
      <template #content>
        <Fluid>
          <div class="flex flex-col gap-4">
            <InputText
              v-model="model.username"
              placeholder="Username"
              :invalid="error"
              @keyup.enter="login()"
            />
            <Password
              v-model="model.password"
              toggle-mask
              :feedback="false"
              placeholder="Password"
              :invalid="error"
              @keyup.enter="login()"
            />
            <Button
              label="Login"
              @click="login()"
            />
          </div>
        </Fluid>
      </template>
    </Card>
  </div>
</template>
