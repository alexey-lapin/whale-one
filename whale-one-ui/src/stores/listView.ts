import { defineStore } from 'pinia'
import { useLocalStorage } from '@vueuse/core'

export const useListViewStore = defineStore('listView', () => {
  const state = useLocalStorage('whale-one/listView', {
    equipment: {
      pageSize: 10,
    },
  })

  return { state }
})
