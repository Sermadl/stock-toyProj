import apiClient from './axios'

export const getMyStocks = async () => {
  const response = await apiClient.get('/market/my-stocks')
  return response.data
}

export const purchaseStock = async (data) => {
  return await apiClient.post('/market/purchase', data)
}

export const sellStock = async (data) => {
  return await apiClient.post('/market/sell', data)
}
