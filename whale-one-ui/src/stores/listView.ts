import { computed } from 'vue'
import { defineStore } from 'pinia'
import { useLocalStorage } from '@vueuse/core'

import type { BaseRefModel } from '@/model/BaseModel.ts'

export const useListViewStore = defineStore('listView', () => {
  const state = useLocalStorage('whale-one/listView', {
    equipmentTypes: {
      ...defaultEquipmentTypesConfig,
    },
    equipment: {
      ...defaultEquipmentConfig,
    },
    equipmentSearch: {
      ...defaultEquipmentSearchConfig,
    },
  } as ListViewConfig)

  const favorites = computed({
    get: () => {
      if (!state.value.equipmentTypes) {
        state.value.equipmentTypes = {
          ...defaultEquipmentTypesConfig,
        }
      }
      if (!state.value.equipmentTypes.favorites) {
        state.value.equipmentTypes.favorites = []
      }
      return state.value.equipmentTypes.favorites
    },
    set: (value: BaseRefModel[]) => {
      state.value.equipmentTypes.favorites = value
    },
  })

  return { state, favorites }
})

export interface EquipmentTypesConfig {
  pageSize: number
  favorites: BaseRefModel[]
}

export interface EquipmentConfig {
  pageSize: number
  showActiveOnly: boolean
}

export interface EquipmentSearchConfig {
  pageSize: number
}

export interface ListViewConfig {
  equipmentTypes: EquipmentTypesConfig
  equipment: EquipmentConfig
  equipmentSearch: EquipmentSearchConfig
}

const defaultEquipmentTypesConfig: EquipmentTypesConfig = {
  pageSize: 10,
  favorites: [],
}

const defaultEquipmentConfig: EquipmentConfig = {
  pageSize: 10,
  showActiveOnly: false,
}

const defaultEquipmentSearchConfig: EquipmentSearchConfig = {
  pageSize: 10,
}
