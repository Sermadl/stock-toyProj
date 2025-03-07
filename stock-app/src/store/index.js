import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useStockStore = defineStore('stock', () => {
  const stocks = ref([])

  const setStocks = (newStocks) => {
    stocks.value = newStocks
  }

  return { stocks, setStocks }
})
