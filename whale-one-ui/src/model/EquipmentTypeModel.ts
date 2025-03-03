import type { EntityHeaderModel } from '@/model/BaseModel.ts'

export interface EquipmentTypeModel extends EntityHeaderModel {
  name: string
}

export interface EquipmentTypeItemModel {
  id: number
  name: string
}
