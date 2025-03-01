import type { ToastMessageOptions } from 'primevue'

const defaultLife = 3000

export const successToast = (detail: string): ToastMessageOptions => {
  return {
    severity: 'success',
    summary: 'Success',
    detail: detail,
    life: defaultLife
  }
}

export const errorToast = (detail: string): ToastMessageOptions => {
  return {
    severity: 'error',
    summary: 'Error',
    detail: detail,
    life: defaultLife
  }
}
