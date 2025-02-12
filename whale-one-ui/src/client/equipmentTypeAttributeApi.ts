import type { ToastServiceMethods } from 'primevue'

import { errorToast, successToast } from '@/utils/toasts.ts'
import { defaultHeaders } from '@/client/baseapi.ts'
import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'

export const invokeAttributeCreateOrUpdate = (
  attribute: EquipmentTypeAttributeModel,
  toast: ToastServiceMethods | null = null) => {
  if (attribute.id > 0) {
    return invokeAttributeUpdate(attribute, toast)
  } else {
    return invokeAttributeCreate(attribute, toast)
  }
}

export const invokeAttributeCreate = (
  attribute: EquipmentTypeAttributeModel,
  toast: ToastServiceMethods | null = null) => {
  return fetch(`/api/equipment/types/${attribute.equipmentTypeId}/attributes`, {
    method: 'POST',
    headers: defaultHeaders,
    body: JSON.stringify(attribute)
  })
    .then(response => {
      if (response.ok) {
        return response.json() as Promise<EquipmentTypeAttributeModel>
      } else {
        throw new Error('Failed to create attribute')
      }
    })
    .then(data => {
      toast?.add(successToast(`Attribute #${data.id} ${data.name} has been created`))
      return data
    })
    .catch(error => {
      toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeAttributeUpdate = (
  attribute: EquipmentTypeAttributeModel,
  toast: ToastServiceMethods | null = null) => {
  return fetch(`/api/equipment/types/${attribute.equipmentTypeId}/attributes/${attribute.id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(attribute)
  })
    .then(response => {
      if (response.ok) {
        return response.json() as Promise<EquipmentTypeAttributeModel>
      } else {
        throw new Error('Failed to update attribute')
      }
    })
    .then(data => {
      toast?.add(successToast(`Attribute #${data.id} ${data.name} has been updated`))
    })
    .catch(error => {
      toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeAttributeDelete = (
  attribute: EquipmentTypeAttributeModel,
  toast: ToastServiceMethods | null = null) => {
  return fetch(`/api/equipment/types/${attribute.equipmentTypeId}/attributes/${attribute.id}`, {
    method: 'DELETE'
  })
    .then(response => {
      if (response.ok) {
        return
      } else {
        throw new Error('Failed to delete attribute')
      }
    })
    .then(data => {
      toast?.add(successToast(`Attribute #${attribute.id} ${attribute.name} has been deleted`))
      return data
    })
    .catch(error => {
      toast?.add(errorToast(error.message))
    })
}
