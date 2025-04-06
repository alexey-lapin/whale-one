import type { BaseRefModel, EntityHeaderModel } from '@/model/BaseModel.ts'

export interface EquipmentTypeNewModel {
  name: string
  description?: string | null
  isAssembly: boolean
  metadata?:
    | (Record<string, unknown> & {
        assemblyParts?: BaseRefModel[]
      })
    | null
}

export interface EquipmentTypeModel extends EntityHeaderModel, EquipmentTypeNewModel {}

export interface EquipmentTypeItemModel {
  id: number
  name: string
}

export interface EquipmentTypeManufacturerModel {
  name: string
  models: string[]
}
