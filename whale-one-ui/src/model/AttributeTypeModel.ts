export interface AttributeTypeModel {
  name: string
  type: 'number' | 'select' | 'text' | 'textarea' | 'files'
  metadata?: (Record<string, unknown> & { options?: string[] | null }) | null
}

export interface AttributeFilterModel {
  field: string
  operator: string
  value: string | string[] | null
}

export interface FilterOperationOptions {
  readonly EQUALS: string
  readonly NOT_EQUALS: string
  readonly CONTAINS: string
  readonly IN: string
}

export const FilterOperation: FilterOperationOptions = {
  EQUALS: '==',
  NOT_EQUALS: '!=',
  CONTAINS: '=ilike=',
  IN: '=in=',
} as const
