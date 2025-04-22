import type { ToastMessageOptions } from 'primevue'
import { AxiosError } from 'axios'

const defaultLife = 3000

export const successToast = (detail: string): ToastMessageOptions => {
  return {
    severity: 'success',
    summary: 'Success',
    detail: detail,
    life: defaultLife,
  }
}

export const errorToast = (error: any): ToastMessageOptions => {
  let summary = 'Error'
  let detail = ''
  if (error instanceof Error) {
    if (error instanceof AxiosError) {
      if (error.response) {
        summary = `${error.response.status}: ${error.response.data.title}`
        detail = error.response.data.detail
      } else if (error.request) {
        detail = `No response received: ${error.message}`
      } else {
        detail = error.message
      }
    } else {
      detail = error.message
    }
  } else if (typeof error === 'string') {
    detail = error
  } else {
    detail = 'Unknown error'
  }
  return {
    severity: 'error',
    summary: summary,
    detail: detail,
    life: defaultLife,
  }
}
