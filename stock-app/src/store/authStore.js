import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getPlayerInfo } from '@/api/player'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const role = ref(localStorage.getItem('role') || 'USER')

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'ADMIN')

  const setAuth = (userToken, userRole) => {
    token.value = userToken
    role.value = userRole
    localStorage.setItem('token', userToken)
    localStorage.setItem('role', userRole)
  }

  const logout = () => {
    token.value = null
    role.value = 'USER'
    localStorage.removeItem('token')
    localStorage.removeItem('role')
  }

  const fetchUserInfo = async () => {
    try {
      const userInfo = await getPlayerInfo()
      role.value = userInfo.role
      console.log('Fetched User Info:', userInfo)
      localStorage.setItem('role', userInfo.role)
    } catch (err) {
      console.error('Failed to fetch user info:', err)
    }
  }

  return { token, role, isAuthenticated, isAdmin, setAuth, logout, fetchUserInfo }
})
