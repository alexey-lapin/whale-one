import type { BaseRefModel, EntityHeaderModel } from '@/model/BaseModel.ts'

export interface EquipmentTypeNewModel {
  name: string
  description?: string | null
  isAssembly: boolean
  isDeployable: boolean
  metadata?:
    | (Record<string, unknown> & {
        assemblyParts?: BaseRefModel[]
      })
    | null
}

export interface EquipmentTypeModel extends EntityHeaderModel, EquipmentTypeNewModel {}

export interface EquipmentTypeItemModel extends BaseRefModel {
  isAssembly: boolean
  isDeployable: boolean
}

export interface EquipmentTypeManufacturerModel {
  name: string
  models: string[]
}
