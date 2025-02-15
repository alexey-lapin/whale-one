import type { BaseRefModel } from '@/model/BaseRefModel.ts'

export interface EquipmentElementModel {
  id: number;
  version: number;
  createdAt: string;
  createdBy: BaseRefModel;
  name: string;
  type: BaseRefModel;
}

export interface EquipmentNewModel {
  name: string | null;
  type: BaseRefModel | null;
}

export interface EquipmentModel extends EquipmentNewModel {
  id: number;
  version: number;
  createdAt: string;
  createdBy: BaseRefModel;
  active: boolean;
  name: string;
  type: BaseRefModel;
  attributes: EquipmentAttributeModel[];
}

export interface EquipmentAttributeModel {
  id: number;
  equipmentTypeAttributeId: number;
  value: string | null;
}
