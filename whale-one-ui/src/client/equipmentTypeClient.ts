import { apiClient, apiClientContext, toFilterQuery } from '@/client/baseClient.ts'
import { errorToast, successToast } from '@/utils/toasts.ts'

import type { BaseRefModel, FilterConditions, PageModel } from '@/model/BaseModel.ts'
import type { EquipmentTypeModel, EquipmentTypeNewModel } from '@/model/EquipmentTypeModel.ts'

export const invokeEquipmentTypeCreate = (
  equipment: EquipmentTypeNewModel,
): Promise<EquipmentTypeModel> => {
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
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeEquipmentTypeUpdate = (
  equipmentType: EquipmentTypeModel,
): Promise<EquipmentTypeModel> => {
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
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeEquipmentTypeGet = (id: number): Promise<EquipmentTypeModel> => {
  return apiClient
    .get<EquipmentTypeModel>(`/api/equipment/types/${id}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeEquipmentTypeListGet = (
  page: number,
  size: number,
  filter: FilterConditions,
): Promise<PageModel<EquipmentTypeModel>> => {
  return apiClient
    .get<PageModel<EquipmentTypeModel>>(
      `/api/equipment/types/search?page=${page}&size=${size}&filters=${toFilterQuery(filter)}`,
    )
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeEquipmentTypeItemListGet = (q?: string | null): Promise<BaseRefModel[]> => {
  return apiClient
    .get<BaseRefModel[]>(`/api/equipment/types/items?q=${q ?? ''}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}
