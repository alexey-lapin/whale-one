import { apiClient, apiClientContext } from '@/client/baseClient.ts'
import type { BaseRefModel, PageModel } from '@/model/BaseModel.ts'
import { errorToast, successToast } from '@/utils/toasts.ts'
import type { EquipmentElementModel, EquipmentModel } from '@/model/EquipmentModel.ts'
import type { EquipmentTypeModel } from '@/model/EquipmentTypeModel.ts'

export const invokeEquipmentTypeCreate = (equipment: EquipmentTypeModel) => {
  return apiClient
    .post<EquipmentTypeModel>('/api/equipment/types', equipment)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Equipment Type #${data.id} ${data.name} has been created`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeEquipmentTypeUpdate = (equipmentType: EquipmentTypeModel) => {
  return apiClient
    .put<EquipmentTypeModel>(`/api/equipment/types/${equipmentType.id}`, equipmentType)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Equipment Type #${data.id} ${data.name} has been updated`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeEquipmentTypeGet = (id: number) => {
  return apiClient
    .get<EquipmentTypeModel>(`/api/equipment/types/${id}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeEquipmentTypeListGet = (page: number, size: number) => {
  return apiClient
    .get<PageModel<EquipmentElementModel>>(`/api/equipment/types?page=${page}&size=${size}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeEquipmentTypeItemListGet = (q?: string | null) => {
  return apiClient
    .get<BaseRefModel[]>(`/api/equipment/types/items?q=${q ?? ''}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}
