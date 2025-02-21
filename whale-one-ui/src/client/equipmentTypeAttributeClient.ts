import { errorToast, successToast } from '@/utils/toasts.ts'
import { apiClient, apiClientContext } from '@/client/baseClient.ts'
import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'

export const invokeAttributeCreateOrUpdate = (attribute: EquipmentTypeAttributeModel) => {
  if (attribute.id > 0) {
    return invokeAttributeUpdate(attribute)
  } else {
    return invokeAttributeCreate(attribute)
  }
}

export const invokeAttributeCreate = (attribute: EquipmentTypeAttributeModel) => {
  return apiClient
    .post<EquipmentTypeAttributeModel>(
      `/api/equipment/types/${attribute.equipmentTypeId}/attributes`,
      attribute,
    )
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Attribute #${data.id} ${data.name} has been created`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeAttributeUpdate = (attribute: EquipmentTypeAttributeModel) => {
  return apiClient
    .put<EquipmentTypeAttributeModel>(
      `/api/equipment/types/${attribute.equipmentTypeId}/attributes/${attribute.id}`,
      attribute,
    )
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Attribute #${data.id} ${data.name} has been updated`),
      )
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeAttributeDelete = (attribute: EquipmentTypeAttributeModel) => {
  return apiClient
    .delete(`/api/equipment/types/${attribute.equipmentTypeId}/attributes/${attribute.id}`)
    .then((data) => {
      apiClientContext.toast?.add(
        successToast(`Attribute #${attribute.id} ${attribute.name} has been deleted`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
    })
}
