import type { EntityHeaderModel } from '@/model/BaseModel.ts'

export interface AnalysisTypeModel extends EntityHeaderModel {
  name: string
  description?: string | null
  metadata?: AnalysisTypeMetadataModel | null
}

export interface AnalysisTypeMetadataModel {
  attributes: AnalysisTypeAttributeModel[]
}

export interface AnalysisTypeAttributeModel {
  name: string
  type: string
  description?: string | null
  metadata?: Record<string, any> | null
}
