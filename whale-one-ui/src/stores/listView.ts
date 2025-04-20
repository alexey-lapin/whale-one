import { defineStore } from 'pinia'
import { useLocalStorage } from '@vueuse/core'
import type { BaseRefModel } from '@/model/BaseModel.ts'

export const useListViewStore = defineStore('listView', () => {
  const state = useLocalStorage('whale-one/listView', {
    equipmentTypes: {
      pageSize: 10,
      favorites: []
    },
    equipment: {
      pageSize: 10,
      showActiveOnly: false,
    },
  } as ListViewConfig)

  return { state }
})

export interface EquipmentTypesConfig {
  pageSize: number
  favorites: BaseRefModel[]
}

export interface EquipmentConfig {
  pageSize: number
  showActiveOnly: boolean
}

export interface ListViewConfig {
  equipmentTypes: EquipmentTypesConfig
  equipment: EquipmentConfig
}
