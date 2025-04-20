import axios from 'axios'
import { useAuthStore } from '@/stores/auth.ts'
import type { ToastServiceMethods } from 'primevue'
import type { FilterConditions } from '@/model/BaseModel.ts'
import { FilterMatchMode, type FilterMatchModeOptions } from '@primevue/core/api'

export const baseURL = import.meta.env.VITE_APP_API_BASE_URL

export const apiClient = axios.create({
  headers: {
    'Content-Type': 'application/json',
  },
})

const getAccessToken = () => {
  const auth = useAuthStore()
  return auth.token?.access_token
}

export const apiClientContext: {
  toast: ToastServiceMethods | undefined
  getAccessToken: () => string | undefined
} = {
  toast: undefined,
  getAccessToken,
}

apiClient.interceptors.request.use(
  (config) => {
    const auth = useAuthStore()
    if (auth.isAuthenticated()) {
      config.headers.Authorization = `Bearer ${getAccessToken()}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      console.log('trying to refresh token')
      const auth = useAuthStore()
      return auth
        .refresh()
        .then(() => {
          console.log('refresh successful')
          return apiClient.request(error.config)
        })
        .catch((refreshError) => {
          console.log('refresh failed')
          return auth.logout().then(() => Promise.reject(refreshError))
        })
    }
    return Promise.reject(error)
  },
)

export const toFilterQuery = (filterConditions: FilterConditions) => {
  const filters: string[] = []
  for (const [field, filterCondition] of Object.entries(filterConditions)) {
    if (filterCondition.value !== null && filterCondition.value !== undefined) {
      switch (filterCondition.matchMode) {
        case FilterMatchMode.STARTS_WITH:
          filters.push(`${field}=startsWith='${filterCondition.value}'`)
          break
        case FilterMatchMode.CONTAINS:
          filters.push(`${field}=ilike='${filterCondition.value}'`)
          break
        case FilterMatchMode.EQUALS:
          filters.push(`${field}=='${filterCondition.value}'`)
          break
        case FilterMatchMode.IN:
          const values = Array.isArray(filterCondition.value)
            ? filterCondition.value.map((i) => `'${i}'`).join(',')
            : `'${filterCondition.value}'`
          filters.push(`${field}=in=(${values})`)
          break
      }
    }
  }
  return filters.join(";")
}
