import axios from 'axios'
import { useAuthStore } from '@/stores/auth.ts'
import type { ToastServiceMethods } from 'primevue'

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
