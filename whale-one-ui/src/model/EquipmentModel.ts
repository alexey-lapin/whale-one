import type { BaseRefModel, EntityHeaderModel } from '@/model/BaseModel.ts'

export interface EquipmentElementModel extends EntityHeaderModel {
  name: string
  type: BaseRefModel
  active: boolean
  status: string
  manufacturer: string
  model: string
  deployment?: BaseRefModel | null
}

export interface EquipmentNewModel {
  name: string | null
  type: BaseRefModel | null
  status: string | null
  manufacturer?: string | null
  model?: string | null
  metadata?: Record<string, any> | null
  assemblyParts?: EquipmentAssemblyPartModel[] | null
}

export interface EquipmentModel extends EquipmentNewModel {
  id: number
  version: number
  createdAt: string
  createdBy: BaseRefModel
  active: boolean
  status: string
  name: string
  type: BaseRefModel
  manufacturer: string
  model: string
  attributes: EquipmentAttributeModel[]
}

export interface EquipmentAssemblyPartModel {
  typeId: number
  equipmentId: number
}

export interface EquipmentAttributeModel {
  id: number
  equipmentTypeAttributeId: number
  value: string | null
}

export interface EquipmentStatusOptions {
  readonly AVAILABLE: string
  readonly BROKEN: string
  readonly DEPLOYED: string
  readonly LOST: string
  readonly MAINTENANCE: string
}

export const EquipmentStatus: EquipmentStatusOptions = {
  AVAILABLE: 'AVAILABLE',
  BROKEN: 'BROKEN',
  DEPLOYED: 'DEPLOYED',
  LOST: 'LOST',
  MAINTENANCE: 'MAINTENANCE',
} as const
