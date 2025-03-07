<template>
  <div>
    <h1>{{ stock.name }}</h1>
    <p>Price: ${{ stock.price }}</p>
    <button @click="purchaseStock">Buy</button>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'

export default {
  setup() {
    const route = useRoute()
    const stock = ref({})

    onMounted(async () => {
      const response = await axios.get(`http://localhost:8080/stock/${route.params.id}`)
      stock.value = response.data
    })

    const purchaseStock = async () => {
      await axios.post('http://localhost:8080/market/purchase', {
        stockId: route.params.id,
        quantity: 1,
      })
      alert('Stock purchased successfully!')
    }

    return { stock, purchaseStock }
  },
}
</script>
