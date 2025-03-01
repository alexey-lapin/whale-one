import type { BaseRefModel } from '@/model/BaseModel.ts'

export interface EquipmentTypeModel {
  id: number;
  version: number;
  createdAt: string,
  createdBy: BaseRefModel,
  name: string;
}

export interface EquipmentTypeItemModel {
  id: number;
  name: string;
}
