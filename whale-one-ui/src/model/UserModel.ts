import type { EntityHeaderModel } from '@/model/BaseModel.ts'

export interface UserModel extends EntityHeaderModel {
  username: string
  enabled: boolean
  authorities: string[]
}

export interface UserNewModel extends UserModel {
  password: string
}
