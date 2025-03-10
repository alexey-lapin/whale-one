import { apiClient, apiClientContext } from '@/client/baseClient.ts'
import { errorToast, successToast } from '@/utils/toasts.ts'
import type { FileMetadata } from '@/model/FileModel.ts'

export interface FileUploadResponse {
  key: string
}

export const DEFAULT_STORE = 'default'

export const invokeFileUpload = (
  file: File,
  description: string,
  storeName: string = DEFAULT_STORE,
) => {
  const formData = new FormData()
  formData.append('file', file)

  // Generate a temporary key for the file
  const tempKey = crypto.randomUUID()

  return apiClient
    .post(`/api/files/${storeName}/${tempKey}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    .then(() => {
      apiClientContext.toast?.add(
        successToast(`File "${file.name}" has been uploaded successfully`),
      )

      // Return complete metadata object that includes server response and local details
      return {
        key: tempKey,
        name: file.name,
        size: file.size,
        type: file.type,
        description: description,
        uploadDate: new Date().toISOString(),
      } as FileMetadata
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(`Failed to upload file: ${error.message}`))
      throw error
    })
}

export const invokeFileDownload = (
  fileMetadata: FileMetadata,
  storeName: string = DEFAULT_STORE,
) => {
  const downloadUrl = `/api/files/${storeName}/${fileMetadata.key}?access_token=${apiClientContext.getAccessToken()}`
  const link = document.createElement('a')
  link.href = downloadUrl
  link.download = fileMetadata.name
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)

  return Promise.resolve(fileMetadata)
}

export const invokeFileDelete = (fileKey: string, storeName: string = DEFAULT_STORE) => {
  return apiClient
    .delete(`/api/files/${storeName}/${fileKey}`, {
      params: { key: fileKey },
    })
    .then(() => {
      apiClientContext.toast?.add(successToast(`File has been deleted`))
      return true
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(`Failed to delete file: ${error.message}`))
      throw error
    })
}
