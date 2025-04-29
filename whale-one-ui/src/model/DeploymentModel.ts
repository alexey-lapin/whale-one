import type { BaseRefModel, EntityHeaderModel } from '@/model/BaseModel.ts'

export interface DeploymentNewModel {
  projectRef: BaseRefModel | null
  projectSiteRef: BaseRefModel | null
  name: string | null
  description: string | null
}

export interface DeploymentModel extends EntityHeaderModel {
  projectRef: BaseRefModel
  projectSiteRef: BaseRefModel
  name: string
  description: string | null
  status: string
  platform: string | null
  providerOrganisations: string | null
  providerParticipants: string | null

  latitude?: number | null
  longitude?: number | null
  bathymetry?: number | null
  deployedAt?: string | null
  deploymentCampaignRef?: BaseRefModel | null
  firstFileAt?: string | null
  lastFileAt?: string | null

  recoveryStatus?: string | null
  recoveredAt?: string | null
  recoveryCampaignRef?: BaseRefModel | null
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

export interface DeploymentEquipmentElementModel {
  id: number
  name: string
  assemblyId?: number | null
  type: BaseRefModel
  assemblyParts: DeploymentEquipmentElementModel[]
}
