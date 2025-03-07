import apiClient from './axios'

export const getAllStocks = async () => {
  const response = await apiClient.get('/stock/all')
  return response.data
}

export const getStockById = async (stockId) => {
  const response = await apiClient.get(`/stock/${stockId}`)
  return response.data
}

export const addStock = async (request) => {
  const response = await apiClient.post('/stock', request)
  return response.data
}
