import { apiClient, apiClientContext } from '@/client/baseClient.ts'
import { errorToast, successToast } from '@/utils/toasts.ts'
import type {
  DeploymentEquipmentElementModel,
  DeploymentEquipmentItemModel,
  DeploymentModel,
  DeploymentNewModel
} from '@/model/DeploymentModel.ts'
import type { EquipmentModel } from '@/model/EquipmentModel.ts'
import type { PageModel } from '@/model/BaseModel.ts'

export const invokeDeploymentCreate = (deployment: DeploymentNewModel) => {
  return apiClient
    .post<DeploymentModel>('/api/deployments', deployment)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Deployment #${data.id} ${data.name} has been created`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeDeploymentUpdate = (deployment: DeploymentModel) => {
  return apiClient
    .put<DeploymentModel>(`/api/deployments/${deployment.id}`, deployment)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Deployment #${data.id} ${data.name} has been updated`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeDeploymentGet = (id: number) => {
  return apiClient
    .get<DeploymentModel>(`/api/deployments/${id}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeDeploymentListGet = (
  page: number,
  size: number,
  name?: string | null,
  projectId?: number | null,
  projectSiteId?: number | null,
  status?: string | null,
) => {
  return apiClient
    .get<PageModel<DeploymentModel>>(
      `/api/deployments?page=${page}&size=${size}&projectId=${projectId ?? ''}&projectSiteId=${projectSiteId ?? ''}&status=${status ?? ''}&name=${name ?? ''}`,
    )
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeDeploymentStatusUpdate = (deploymentId: number, status: string) => {
  return apiClient
    .put<DeploymentModel>(`/api/deployments/${deploymentId}/status?status=${status}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeDeploymentEquipmentAdd = (deploymentId: number, equipmentId: number) => {
  return apiClient
    .post<EquipmentModel>(`/api/deployments/${deploymentId}/equipment`, {
      deploymentId,
      equipmentId,
    })
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeDeploymentEquipmentListGet = (deploymentId: number) => {
  return apiClient
    .get<DeploymentEquipmentItemModel[]>(`/api/deployments/${deploymentId}/equipment`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeDeploymentEquipmentElementsGet = (deploymentId: number) => {
  return apiClient
    .get<DeploymentEquipmentElementModel[]>(`/api/deployments/${deploymentId}/equipment/elements`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeDeploymentEquipmentDelete = (deploymentId: number, equipmentId: number) => {
  return apiClient
    .delete<EquipmentModel>(`/api/deployments/${deploymentId}/equipment/${equipmentId}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}
