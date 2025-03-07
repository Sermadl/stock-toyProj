import apiClient from './axios'

export const login = async (credentials) => {
  const response = await apiClient.post('/player/login', credentials)
  return response.data
}

export const logout = async () => {
  const response = await apiClient.post('/player/logout')
  return response.data
}
export const register = async (userData) => {
  const response = await apiClient.post('/player/register', userData)
  return response.data
}
