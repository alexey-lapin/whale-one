import type { Ref } from 'vue'
import { computed } from 'vue'
import { defineStore } from 'pinia'
import { useJwt } from '@vueuse/integrations/useJwt'
import { StorageSerializers, useLocalStorage } from '@vueuse/core'
import type { JwtPayload } from 'jwt-decode'
import router from '@/router'

interface ExtendedJwtPayload extends JwtPayload {
  userId: number
  scope: string[]
}

interface TokenModel {
  access_token: string
  refresh_token: string
  token_type: string
  expires_in: number
}

export const useAuthStore = defineStore('auth', () => {
  const token: Ref<TokenModel | null> = useLocalStorage('whale-one/auth/token', null, {
    serializer: StorageSerializers.object,
  })

  const jwt = computed(() => {
    return token.value ? useJwt<ExtendedJwtPayload>(token.value.access_token, {}) : null
  })

  function isAuthenticated() {
    return token.value !== null
  }

  const login = (username: string, password: string, redirect?: string) => {
    fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    })
      .then((res) => res.json())
      .then((data) => {
        token.value = data
        router.push({ path: redirect ?? '/' })
      })
  }

  const refresh = () => {
    return fetch('/api/auth/refresh', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token.value?.refresh_token}`,
      },
    })
      .then((res) => {
        if (res.ok) {
          return res
        } else {
          throw new Error("Session expired - please login again")
        }
      })
      .then((res) => res.json())
      .then((data) => {
        token.value = data
      })
  }

  const logout = () => {
    token.value = null
    let redirect = ''
    if (router.currentRoute.value.name !== 'login') {
      redirect = router.currentRoute.value.fullPath
    }
    return router.push({ name: 'login', query: { redirect } })
  }

  const username = computed(() => jwt.value?.payload.value?.sub)
  const userId = computed(() => jwt.value?.payload.value?.userId)
  const hasAuthority = (authority: string) => jwt.value?.payload.value?.scope.includes(authority)

  return { token, isAuthenticated, login, refresh, logout, username, userId, hasAuthority }
})
