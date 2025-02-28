import { apiClient, apiClientContext } from '@/client/baseClient.ts'
import type { BaseRefModel, PageModel } from '@/model/BaseModel.ts'
import { errorToast, successToast } from '@/utils/toasts.ts'
import type {
  EquipmentElementModel,
  EquipmentModel,
  EquipmentNewModel,
} from '@/model/EquipmentModel.ts'

export const invokeEquipmentCreate = (equipment: EquipmentNewModel) => {
  return apiClient
    .post<EquipmentModel>('/api/equipment', equipment)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Equipment #${data.id} ${data.name} has been created`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeEquipmentUpdate = (equipment: EquipmentModel) => {
  return apiClient
    .put<EquipmentModel>(`/api/equipment/${equipment.id}`, equipment)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Equipment #${data.id} ${data.name} has been updated`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeEquipmentGet = (id: number) => {
  return apiClient
    .get<EquipmentModel>(`/api/equipment/${id}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeEquipmentListGet = (
  page: number,
  size: number,
  typeId?: number | null,
  name?: string | null,
) => {
  return apiClient
    .get<PageModel<EquipmentElementModel>>(
      `/api/equipment?page=${page}&size=${size}&typeId=${typeId ?? ''}&name=${name ?? ''}`,
    )
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeEquipmentItemListGet = (typeId: number, q?: string | null) => {
  return apiClient
    .get<BaseRefModel[]>(`/api/equipment/items?typeId=${typeId}&q=${q ?? ''}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}
