import apiClient from './axios'

export const getPlayerInfo = async () => {
  const response = await apiClient.get('/player/info')
  return response.data
}

export const deposit = async (data) => {
  return await apiClient.post('/player/deposit', data)
}

export const withdraw = async (data) => {
  return await apiClient.post('/player/withdraw', data)
}
