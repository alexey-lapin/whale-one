import { errorToast, successToast } from '@/utils/toasts.ts'
import { apiClient, apiClientContext } from '@/client/baseClient.ts'

import type { ProjectCampaignModel, ProjectModel, ProjectSiteModel } from '@/model/ProjectModel.ts'
import type { BaseRefModel, PageModel } from '@/model/BaseModel.ts'

export const invokeProjectCreate = (project: ProjectModel) => {
  return apiClient
    .post<ProjectModel>('/api/projects', project)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(successToast(`Project #${data.id} ${data.name} has been created`))
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeProjectUpdate = (project: ProjectModel) => {
  return apiClient
    .put<ProjectModel>(`/api/projects/${project.id}`, project)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(successToast(`Project #${data.id} ${data.name} has been updated`))
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeProjectGet = (id: number) => {
  return apiClient
    .get<ProjectModel>(`/api/projects/${id}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeProjectListGet = (page: number, size: number) => {
  return apiClient
    .get<PageModel<ProjectModel>>(`/api/projects?page=${page}&size=${size}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeProjectItemListGet = (q: string | null) => {
  return apiClient
    .get<BaseRefModel[]>(`/api/projects/items?q=${q}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeSiteCreateOrUpdate = (site: ProjectSiteModel) => {
  if (site.id > 0) {
    return invokeSiteUpdate(site)
  } else {
    return invokeSiteCreate(site)
  }
}

export const invokeSiteCreate = (site: ProjectSiteModel) => {
  return apiClient
    .post<ProjectSiteModel>(`/api/projects/${site.projectId}/sites`, site)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(successToast(`Site #${data.id} ${data.name} has been created`))
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeSiteUpdate = (site: ProjectSiteModel) => {
  return apiClient
    .put<ProjectSiteModel>(`/api/projects/${site.projectId}/sites/${site.id}`, site)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(successToast(`Site #${data.id} ${data.name} has been updated`))
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeSiteListGet = (projectId: number) => {
  return apiClient
    .get<ProjectSiteModel[]>(`/api/projects/${projectId}/sites`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeSiteItemListGet = (projectId: number, q: string | null) => {
  return apiClient
    .get<BaseRefModel[]>(`/api/projects/${projectId}/sites/items?q=${q ?? ''}`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeSiteDelete = (site: ProjectSiteModel) => {
  return apiClient
    .delete(`/api/projects/${site.projectId}/sites/${site.id}`)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(successToast(`Site #${site.id} ${site.name} has been deleted`))
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeCampaignCreateOrUpdate = (campaign: ProjectCampaignModel) => {
  if (campaign.id > 0) {
    return invokeCampaignUpdate(campaign)
  } else {
    return invokeCampaignCreate(campaign)
  }
}

export const invokeCampaignCreate = (campaign: ProjectCampaignModel) => {
  return apiClient
    .post<ProjectCampaignModel>(`/api/projects/${campaign.projectId}/campaigns`, campaign)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Campaign #${data.id} ${data.name} has been created`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeCampaignUpdate = (campaign: ProjectCampaignModel) => {
  return apiClient
    .put<ProjectCampaignModel>(
      `/api/projects/${campaign.projectId}/campaigns/${campaign.id}`,
      campaign,
    )
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Campaign #${data.id} ${data.name} has been updated`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeCampaignListGet = (projectId: number) => {
  return apiClient
    .get<ProjectCampaignModel[]>(`/api/projects/${projectId}/campaigns`)
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}

export const invokeCampaignDelete = (campaign: ProjectCampaignModel) => {
  return apiClient
    .delete(`/api/projects/${campaign.projectId}/campaigns/${campaign.id}`)
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Campaign #${campaign.id} ${campaign.name} has been deleted`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error))
      throw error
    })
}
