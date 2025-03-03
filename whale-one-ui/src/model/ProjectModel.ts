import type { EntityHeaderModel } from '@/model/BaseModel.ts'

export interface ProjectModel extends EntityHeaderModel {
  name: string
  client: string | null
  ownership: string | null
  region: string | null
  type: string | null
  goal: string | null
  description: string | null
}

export interface ProjectSiteModel {
  id: number
  projectId: number
  name: string
  longitude: number | null
  latitude: number | null
  depth: number | null
}

export interface ProjectCampaignModel {
  id: number
  projectId: number
  name: string
}
