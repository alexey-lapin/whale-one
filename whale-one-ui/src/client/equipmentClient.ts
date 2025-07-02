import {
  apiClient,
  apiClientContext,
  ApiError,
  toAttrFilterQuery,
  toFilterQuery,
} from '@/client/baseClient.ts'
import { errorToast, successToast } from '@/utils/toasts.ts'

import type { BaseRefModel, FilterConditions, PageModel } from '@/model/BaseModel.ts'
import type {
  EquipmentElementModel,
  EquipmentModel,
  EquipmentNewModel,
} from '@/model/EquipmentModel.ts'
import type { FilterModel } from '@/model/AttributeTypeModel.ts'

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
      apiClientContext.toast?.add(errorToast(error))
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
      apiClientContext.toast?.add(errorToast(error))
      throw new ApiError(error)
    })
}

export const invokeEquipmentToggleActive = (id: number) => {
  return apiClient
    .put<EquipmentModel>(`/api/equipment/${id}/active`)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(
          `Equipment #${data.id} ${data.name} has been ${data.active ? 'activated' : 'deactivated'}`,
        ),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeEquipmentGet = (id: number) => {
  return apiClient
    .get<EquipmentModel>(`/api/equipment/${id}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeEquipmentFindByDescriptor = (descriptor: string) => {
  return apiClient
    .get<BaseRefModel>(`/api/equipment/descriptor/${descriptor}`)
    .then((response) => response.data)
}

export const invokeEquipmentListGet = (page: number, size: number, filter: FilterConditions) => {
  return apiClient
    .get<PageModel<EquipmentElementModel>>(
      `/api/equipment?page=${page}&size=${size}&filters=${toFilterQuery(filter)}`,
    )
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeEquipmentItemGet = (id: number) => {
  return apiClient
    .get<BaseRefModel>(`/api/equipment/items/${id}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeEquipmentItemListGet = (typeId: number, q?: string | null) => {
  return apiClient
    .get<BaseRefModel[]>(`/api/equipment/items?typeId=${typeId}&q=${q ?? ''}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeEquipmentDelete = (id: number) => {
  return apiClient
    .delete(`/api/equipment/${id}`)
    .then(() => {
      apiClientContext.toast?.add(successToast(`Equipment #${id} has been deleted`))
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeEquipmentSearch = (
  page: number,
  size: number,
  filters: FilterModel[],
) => {
  return apiClient
    .get<PageModel<EquipmentModel>>(
      `/api/equipment/search?page=${page}&size=${size}&filters=${toAttrFilterQuery(filters)}`,
    )
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeExportDownload = (filters: FilterModel[]) => {
  const downloadUrl = `/api/equipment/export?filters=${toAttrFilterQuery(filters)}&access_token=${apiClientContext.getAccessToken()}`
  const link = document.createElement('a')
  link.href = downloadUrl
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  return Promise.resolve()
}
