<script setup lang="ts">
import { onMounted, ref, type Ref } from 'vue'

import Avatar from 'primevue/avatar'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import FloatLabel from 'primevue/floatlabel'
import InputText from 'primevue/inputtext'
import Panel from 'primevue/panel'
import Password from 'primevue/password'
import ToggleSwitch from 'primevue/toggleswitch'

import EntityHeader from '@/components/EntityHeader.vue'

import { invokeUserGet, invokeUserPasswordUpdate, invokeUserUpdate } from '@/client/userClient.ts'

import type { UserModel } from '@/model/UserModel.ts'

const props = defineProps<{
  id: number
}>()

const model: Ref<UserModel | null> = ref(null)

const loading = ref(false)
const editing = ref(false)

const passwordDialogVisible = ref(false)
const passwordDialogValue = ref('')

const getUser = () => {
  invokeUserGet(props.id)
    .then((data) => (model.value = data))
    .catch(() => {})
}

const updateUser = () => {
  if (model.value === null) {
    return
  }
  loading.value = true
  invokeUserUpdate(model.value)
    .then((data) => {
      model.value = data
      editing.value = false
    })
    .catch(() => {})
    .finally(() => {
      loading.value = false
    })
}

const passwordDialogSave = () => {
  if (model.value === null) {
    return
  }
  invokeUserPasswordUpdate(props.id, model.value.version, passwordDialogValue.value)
    .then((data) => {
      model.value = data
      passwordDialogValue.value = ''
      passwordDialogVisible.value = false
    })
    .catch(() => {})
}

const passwordDialogCancel = () => {
  passwordDialogValue.value = ''
  passwordDialogVisible.value = false
}

onMounted(() => {
  getUser()
})
</script>

<template>
  <div
    v-if="model"
    class="mt-5"
  >
    <div class="flex flex-col gap-5 my-4">
      <EntityHeader
        :header="`User #${model.id}`"
        :model="model"
      />

      <Panel header="Info">
        <template #icons>
          <div class="flex gap-2">
            <Button
              severity="secondary"
              size="small"
              icon="pi pi-key"
              @click="passwordDialogVisible = !passwordDialogVisible"
            />
            <Button
              severity="secondary"
              size="small"
              icon="pi pi-pencil"
              @click="editing = !editing"
            />
          </div>
        </template>
        <template #default>
          <div class="mt-1 flex flex-col gap-3">
            <FloatLabel variant="on">
              <InputText
                id="name"
                v-model="model.username"
                :disabled="!editing"
              />
              <label for="name">Username</label>
            </FloatLabel>

            <ToggleSwitch
              class="mb-1"
              v-model="model.enabled"
              :disabled="!editing"
            />

            <!--            <FloatLabel variant="on">-->
            <!--              <Password-->
            <!--                v-model="model.password"-->
            <!--                toogle-mask-->
            <!--              />-->
            <!--              <label for="1name">Password</label>-->
            <!--            </FloatLabel>-->
          </div>
          <Button
            v-if="editing"
            label="Save"
            icon="pi pi-save"
            class="mt-4"
            :loading="loading"
            @click="updateUser()"
          />
        </template>
      </Panel>
    </div>
  </div>

  <Dialog
    v-model:visible="passwordDialogVisible"
    modal
    header="Update Password"
    :style="{ width: '25rem' }"
  >
    <template #header>
      <div class="inline-flex items-center justify-center gap-2">
        <Avatar
          :label="model?.username.charAt(0).toUpperCase()"
          shape="circle"
        />
        <span class="font-bold whitespace-nowrap">{{ model?.username }}</span>
      </div>
    </template>
    <span class="text-surface-500 dark:text-surface-400 block mb-8">Update password</span>
    <div class="flex items-center gap-4 mb-2">
      <label
        for="email"
        class="font-semibold w-24"
        >Password</label
      >
      <Password
        v-model="passwordDialogValue"
        toggle-mask
      />
    </div>
    <template #footer>
      <Button
        label="Cancel"
        text
        severity="secondary"
        @click="passwordDialogCancel()"
        autofocus
      />
      <Button
        label="Save"
        @click="passwordDialogSave()"
        autofocus
      />
    </template>
  </Dialog>
</template>
