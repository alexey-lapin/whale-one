import type { EntityHeaderModel } from '@/model/BaseModel.ts'

export interface EquipmentTypeModel extends EntityHeaderModel {
  name: string
  description?: string | null
  metadata?: Record<string, unknown>
}

export interface EquipmentTypeItemModel {
  id: number
  name: string
}

export interface EquipmentTypeManufacturerModel {
  name: string,
  models: string[]
}
