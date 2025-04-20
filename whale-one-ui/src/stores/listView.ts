import { defineStore } from 'pinia'
import { useLocalStorage } from '@vueuse/core'

export const useListViewStore = defineStore('listView', () => {
  const state = useLocalStorage('whale-one/listView', {
    equipmentTypes: {
      pageSize: 10,
    },
    equipment: {
      pageSize: 10,
      showActiveOnly: false,
    },
  })

  return { state }
})
