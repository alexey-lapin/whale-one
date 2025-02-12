import type { BaseRefModel } from '@/model/BaseRefModel.ts'

export interface EquipmentElementModel {
  id: number;
  version: number;
  createdAt: string;
  createdBy: string;
  name: string;
  type: BaseRefModel;
}

export interface EquipmentModel {
  id: number;
  version: number;
  createdAt: string;
  createdBy: string;
  name: string;
  type: number;
  attributes: EquipmentAttributeModel[];
}

export interface EquipmentAttributeModel {
  id: number;
  equipmentTypeAttributeId: number;
  value: string;
}
