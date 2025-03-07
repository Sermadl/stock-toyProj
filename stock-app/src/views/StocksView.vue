<template>
  <div>
    <h1>Stocks</h1>
    <ul>
      <li v-for="stock in stocks" :key="stock.id">
        <router-link :to="`/stocks/${stock.id}`">{{ stock.name }} - ${{ stock.price }}</router-link>
      </li>
    </ul>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getAllStocks } from '@/api/stock'

export default {
  setup() {
    const stocks = ref([])

    onMounted(async () => {
      stocks.value = await getAllStocks()
    })

    return { stocks }
  },
}
</script>
