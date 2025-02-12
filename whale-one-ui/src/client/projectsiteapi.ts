import type { ToastServiceMethods } from 'primevue'

import type ProjectSiteModel from '@/model/ProjectSiteModel.ts'
import { errorToast, successToast } from '@/utils/toasts.ts'
import { defaultHeaders } from '@/client/baseapi.ts'

export const invokeSiteCreateOrUpdate = (
  site: ProjectSiteModel,
  toast: ToastServiceMethods | null = null) => {
  if (site.id > 0) {
    return invokeSiteUpdate(site, toast)
  } else {
    return invokeSiteCreate(site, toast)
  }
}

export const invokeSiteCreate = (
  site: ProjectSiteModel,
  toast: ToastServiceMethods | null = null) => {
  return fetch(`/api/projects/${site.projectId}/sites`, {
    method: 'POST',
    headers: defaultHeaders,
    body: JSON.stringify(site)
  })
    .then(response => {
      if (response.ok) {
        return response.json() as Promise<ProjectSiteModel>
      } else {
        throw new Error('Failed to create site')
      }
    })
    .then(data => {
      toast?.add(successToast(`Site #${data.id} ${data.name} has been created`))
      return data
    })
    .catch(error => {
      toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeSiteUpdate = (
  site: ProjectSiteModel,
  toast: ToastServiceMethods | null = null) => {
  return fetch(`/api/projects/${site.projectId}/sites/${site.id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(site)
  })
    .then(response => {
      if (response.ok) {
        return response.json() as Promise<ProjectSiteModel>
      } else {
        throw new Error('Failed to update site')
      }
    })
    .then(data => {
      toast?.add(successToast(`Site #${data.id} ${data.name} has been updated`))
    })
    .catch(error => {
      toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeSiteDelete = (
  site: ProjectSiteModel,
  toast: ToastServiceMethods | null = null) => {
  return fetch(`/api/projects/${site.projectId}/sites/${site.id}`, {
    method: 'DELETE'
  })
    .then(response => {
      if (response.ok) {
        return
      } else {
        throw new Error('Failed to delete site')
      }
    })
    .then(data => {
      toast?.add(successToast(`Site #${site.id} ${site.name} has been deleted`))
      return data
    })
    .catch(error => {
      toast?.add(errorToast(error.message))
    })
}
