<script setup lang="ts">
import { ref, watch } from 'vue'

import Button from 'primevue/button'
import Card from 'primevue/card'
import Dialog from 'primevue/dialog'
import FileUpload from 'primevue/fileupload'
import Tag from 'primevue/tag'
import Textarea from 'primevue/textarea'

import { invokeFileDelete, invokeFileDownload, invokeFileUpload } from '@/client/fileClient.ts'

import type { FileMetadata } from '@/model/FileModel.ts'
import dayjs from 'dayjs'

const model = defineModel<string | null>({ required: true })
defineProps<{
  attributeName: string
  editing: boolean
}>()

const uploadDialogVisible = ref(false)
const selectedFile = ref<File | null>(null)
const fileDescription = ref('')
const files = ref<FileMetadata[]>([])
const isUploading = ref(false)

watch(
  () => model.value,
  (newValue) => {
    if (newValue) {
      try {
        files.value = JSON.parse(newValue)
      } catch (e) {
        files.value = []
      }
    } else {
      files.value = []
    }
  },
  { immediate: true },
)

watch(
  () => files.value,
  (newValue) => {
    if (newValue.length === 0) {
      model.value = null
      return
    }
    model.value = JSON.stringify(newValue)
  },
  { deep: true },
)

const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const uploadFile = (event: any) => {
  if (event.files && event.files.length > 0) {
    selectedFile.value = event.files[0]
  }
}

const confirmUpload = async () => {
  if (selectedFile.value) {
    isUploading.value = true

    try {
      const fileMetadata = await invokeFileUpload(selectedFile.value, fileDescription.value)

      files.value.push(fileMetadata)
      closeUploadDialog()
    } catch (error) {
      console.error('Error uploading file:', error)
    } finally {
      isUploading.value = false
    }
  }
}

const removeFile = async (index: number, fileKey: string) => {
  try {
    await invokeFileDelete(fileKey)
    files.value.splice(index, 1)
  } catch (error) {
    console.error('Error deleting file:', error)
  }
}

const openUploadDialog = () => {
  uploadDialogVisible.value = true
  selectedFile.value = null
  fileDescription.value = ''
}

const closeUploadDialog = () => {
  uploadDialogVisible.value = false
  selectedFile.value = null
  fileDescription.value = ''
}

const downloadFile = (file: FileMetadata) => {
  invokeFileDownload(file)
}
</script>

<template>
  <Card class="w-full">
    <template #subtitle>
      <span>{{ attributeName }}</span>
    </template>
    <template #content>
      <div class="flex flex-col gap-2">
        <div
          v-if="files.length === 0"
          class="text-center p-2 text-gray-500"
        >
          No files uploaded
        </div>
        <div
          v-else
          class="flex flex-col gap-3"
        >
          <div
            v-for="(file, index) in files"
            :key="index"
            class="p-3 border border-gray-200 rounded-md"
          >
            <div class="flex items-center justify-between">
              <div class="flex flex-col gap-1">
                <div class="flex items-center gap-2">
                  <i class="pi pi-file"></i>
                  <span class="font-medium">{{ file.name }}</span>
                  <Tag
                    :value="file.type.split('/')[1]"
                    severity="info"
                  />
                </div>
                <div class="text-sm text-gray-600">
                  {{ formatFileSize(file.size) }} · Uploaded
                  {{ dayjs(file.uploadDate).format('YYYY-MM-DD') }}
                </div>
                <div
                  v-if="file.description"
                  class="text-sm mt-1 text-gray-700"
                >
                  {{ file.description }}
                </div>
              </div>
              <div class="flex gap-1">
                <Button
                  icon="pi pi-download"
                  text
                  severity="secondary"
                  @click="downloadFile(file)"
                />
                <Button
                  v-if="editing"
                  icon="pi pi-trash"
                  text
                  severity="secondary"
                  class="hover:!text-red-600"
                  @click="removeFile(index, file.key)"
                />
              </div>
            </div>
          </div>
        </div>

        <div
          v-if="editing"
          class="flex justify-end"
        >
          <Button
            icon="pi pi-upload"
            label="Upload File"
            severity="secondary"
            @click="openUploadDialog"
          />
        </div>
      </div>

      <Dialog
        v-model:visible="uploadDialogVisible"
        modal
        header="Upload File"
        :style="{ width: '30rem' }"
        :closable="false"
      >
        <div class="flex flex-col gap-4">
          <FileUpload
            mode="basic"
            :multiple="false"
            @select="uploadFile"
            chooseLabel="Select File"
            class="w-full"
          />

          <div
            v-if="selectedFile"
            class="p-3 bg-gray-50 rounded-md"
          >
            <div class="flex items-center gap-2">
              <i class="pi pi-file"></i>
              <span>{{ selectedFile.name }}</span>
              <span class="text-sm text-gray-500">({{ formatFileSize(selectedFile.size) }})</span>
            </div>
          </div>

          <div class="flex flex-col gap-2">
            <label for="file-description">Description</label>
            <Textarea
              id="file-description"
              v-model="fileDescription"
              rows="3"
              class="w-full"
            />
          </div>
        </div>

        <template #footer>
          <Button
            label="Cancel"
            text
            @click="closeUploadDialog"
          />
          <Button
            label="Upload"
            :disabled="!selectedFile"
            @click="confirmUpload"
            severity="primary"
            autofocus
          />
        </template>
      </Dialog>
    </template>
  </Card>
</template>

<style scoped>
:deep(.p-card-body) {
  @apply p-2;
}
</style>
