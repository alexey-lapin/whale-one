import { apiClient, apiClientContext } from '@/client/baseClient.ts'
import { errorToast, successToast } from '@/utils/toasts.ts'

import type { AnalysisTypeModel } from '@/model/AnalysisTypeModel.ts'
import type { PageModel } from '@/model/BaseModel.ts'

export const invokeAnalysisTypeCreate = (analysisType: AnalysisTypeModel) => {
  return apiClient
    .post<AnalysisTypeModel>('/api/analysis/types', analysisType)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Analysis Type #${data.id} ${data.name} has been created`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeAnalysisTypeUpdate = (analysisType: AnalysisTypeModel) => {
  return apiClient
    .put<AnalysisTypeModel>(`/api/analysis/types/${analysisType.id}`, analysisType)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Analysis Type #${data.id} ${data.name} has been updated`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeAnalysisTypeGet = (id: number) => {
  return apiClient
    .get<AnalysisTypeModel>(`/api/analysis/types/${id}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeAnalysisTypeListGet = (page: number, size: number) => {
  return apiClient
    .get<PageModel<AnalysisTypeModel>>(`/api/analysis/types?page=${page}&size=${size}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}
