export const deleteConfirm = (message: string, accept: () => void) => {
  return {
    message: message,
    header: 'Confirmation',
    icon: 'pi pi-info-circle',
    rejectLabel: 'Cancel',
    rejectProps: {
      label: 'Cancel',
      severity: 'secondary',
      outlined: true
    },
    acceptProps: {
      label: 'Delete',
      severity: 'danger'
    },
    accept: accept
  }
}
