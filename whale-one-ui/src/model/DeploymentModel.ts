import type { BaseRefModel } from '@/model/BaseModel.ts'

export interface DeploymentNewModel {
  projectRef: BaseRefModel | null
  projectSiteRef: BaseRefModel | null
  name: string | null
  description: string | null
}

export interface DeploymentModel {
  id: number
  version: number,
  createdAt: string
  createdBy: BaseRefModel
  projectRef: BaseRefModel
  projectSiteRef: BaseRefModel
  name: string
  description: string | null
  status: string
  platform: string | null
  providerOrganisations: string | null
  providerParticipants: string | null
}

export interface DeploymentEquipmentModel {
  deploymentId: number
  equipmentId: number
}

export interface DeploymentEquipmentItemModel {
  deploymentId: number
  equipmentTypeRef: BaseRefModel
  equipmentRef: BaseRefModel
}
