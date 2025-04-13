import type { BaseRefModel, EntityHeaderModel } from '@/model/BaseModel.ts'

export interface EquipmentElementModel extends EntityHeaderModel {
  name: string
  type: BaseRefModel
  manufacturer: string
  model: string
}

export interface EquipmentNewModel {
  name: string | null
  type: BaseRefModel | null
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
