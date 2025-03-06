import { apiClient, apiClientContext } from '@/client/baseClient.ts'
import { errorToast, successToast } from '@/utils/toasts.ts'

import type { PageModel } from '@/model/BaseModel.ts'
import type { UserModel } from '@/model/UserModel.ts'

export const invokeUserCreate = (project: UserModel) => {
  return apiClient
    .post<UserModel>('/api/users', project)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`User #${data.id} ${data.username} has been created`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeUserUpdate = (project: UserModel) => {
  return apiClient
    .put<UserModel>(`/api/users/${project.id}`, project)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`User #${data.id} ${data.username} has been updated`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeUserPasswordUpdate = (id: number, version: number, password: string) => {
  return apiClient
    .put<UserModel>(`/api/users/${id}/password`, { version, password })
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`User #${data.id} ${data.username} password has been updated`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeUserGet = (id: number) => {
  return apiClient
    .get<UserModel>(`/api/users/${id}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeUserListGet = (page: number, size: number) => {
  return apiClient
    .get<PageModel<UserModel>>(`/api/users?page=${page}&size=${size}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}
