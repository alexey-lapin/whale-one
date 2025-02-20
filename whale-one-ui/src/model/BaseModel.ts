export interface PageModel<T> {
  items: T[]
  number: number
  size: number
  totalElements: number
}

export interface BaseRefModel {
  id: number
  name: string
}

export interface EntityHeaderModel {
  id: number
  version: number
  createdAt: string
  createdBy: BaseRefModel
  lastUpdatedAt: string
  lastUpdatedBy: BaseRefModel
}
