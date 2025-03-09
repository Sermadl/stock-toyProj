// components/PortfolioView.vue
<template>
  <div class="portfolio-container">
    <h2>My Portfolio</h2>

    <div class="loading-message" v-if="loading">Loading portfolio...</div>
    <div class="error-message" v-if="error">{{ error }}</div>

    <div v-if="!loading && !error">
      <div class="empty-message" v-if="stocks.length === 0">
        You don't own any stocks yet. Visit the Market to start investing!
      </div>

      <div class="stock-list" v-else>
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Code</th>
              <th>Purchase Price</th>
              <th>Current Price</th>
              <th>Rate of Return</th>
              <th>Quantity</th>
              <th>Total Value</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="stock in stocks" :key="stock.code">
              <td>{{ stock.name }}</td>
              <td>{{ stock.code }}</td>
              <td>{{ stock.price.toLocaleString() }}</td>
              <td>{{ stock.currentPrice }}</td>
              <td :class="getReturnRateClass(stock)">{{ calculateReturnRate(stock) }}%</td>
              <td>{{ stock.quantity }}</td>
              <td>{{ (stock.currentPrice * stock.quantity).toLocaleString() }}</td>
              <td>
                <button @click="showSellModal(stock)">Sell</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Sell Modal -->
    <div class="modal" v-if="show">
      <div class="modal-content">
        <span class="close" @click="show = false">&times;</span>
        <h3>Sell {{ sellModal.name }}</h3>
        <p>Current Price: {{ sellModal.price }}</p>
        <p>You own: {{ sellModal.stock.quantity }}</p>

        <form @submit.prevent="handleSellStock">
          <div class="form-group">
            <label for="price">Price</label>
            <input id="price" v-model.number="sellData.price" type="number" min="1" required />
          </div>

          <div class="form-group">
            <label for="sell-quantity">Quantity to Sell</label>
            <input
              id="sell-quantity"
              v-model.number="sellData.quantity"
              type="number"
              :min="1"
              :max="sellModal.stock.quantity"
              required
            />
          </div>

          <div class="form-group">
            <label for="sell-password">Confirm Password</label>
            <input id="sell-password" v-model="sellData.password" type="password" required />
          </div>

          <div class="total-value">
            Total Value: {{ (sellData.quantity * sellModal.stock.price).toLocaleString() }}
          </div>

          <button type="submit" class="submit-btn sell-btn">Confirm Sale</button>
          <div v-if="sellError" class="error-message">{{ sellError }}</div>
          <div v-if="sellSuccess" class="success-message">Sale successful!</div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getMyStocks, sellStock } from '@/api/market'

export default {
  name: 'PortfolioView',
  setup() {
    const stocks = ref([])
    const loading = ref(true)
    const error = ref('')
    let eventSource = null

    const sellModal = ref({
      name: '',
      code: '',
      price: 0,
      quantity: 0,
    })

    const sellData = ref({
      password: '',
      stockId: 0,
      quantity: 1,
    })

    const sellError = ref('')
    const sellSuccess = ref(false)
    const show = ref(false)

    // SSE 설정
    const setupEventSource = () => {
      if (eventSource) {
        eventSource.close()
      }
      eventSource = new EventSource('http://localhost:8080/stock/price')

      eventSource.addEventListener('stock-update', (event) => {
        try {
          console.log('Received SSE data: ', event.data)

          const newStocks = JSON.parse(event.data)
          newStocks.forEach((stockUpdate) => {
            const stock = stocks.value.find((s) => s.id === stockUpdate.id)
            if (stock) {
              stock.currentPrice = stockUpdate.currentPrice
              stock.percentChange = stockUpdate.percentChange

              if (show.value && sellData.value && sellData.value.stockId === stockUpdate.id) {
                sellModal.value.price = stockUpdate.currentPrice
                // Don't automatically update the user's input price
              }
            }
          })
        } catch (err) {
          console.error('Error parsing SSE data:', err)
        }
      })

      eventSource.onerror = () => {
        console.error('SSE connection error')
        eventSource.close()
        setTimeout(setupEventSource, 3000)
      }
    }

    onMounted(() => {
      setupEventSource()
    })

    const calculateReturnRate = (stock) => {
      if (!stock.currentPrice || !stock.price) return '0.00'
      const returnRate = ((stock.currentPrice - stock.price) / stock.price) * 100
      return returnRate.toFixed(2)
    }

    const getReturnRateClass = (stock) => {
      const returnRate = calculateReturnRate(stock)
      return parseFloat(returnRate) < 0
        ? 'price-down'
        : parseFloat(returnRate) > 0
          ? 'price-up'
          : ''
    }

    const loadPortfolio = async () => {
      try {
        loading.value = true
        error.value = ''
        stocks.value = await getMyStocks()
      } catch (err) {
        console.error('Error loading portfolio:', err)
        error.value = 'Failed to load your portfolio. Please try again later.'
      } finally {
        loading.value = false
      }
    }

    onMounted(loadPortfolio)

    const showSellModal = (stock) => {
      show.value = true
      sellModal.value = {
        stock: stock,
        quantity: 1,
        password: '',
      }
      sellData.value = {
        stockId: stock.id,
        price: 0,
        quantity: 1,
        password: '',
      }
    }

    const handleSellStock = async () => {
      try {
        sellError.value = ''
        sellSuccess.value = false

        await sellStock(sellData.value)

        sellSuccess.value = true

        // Reload portfolio data
        await loadPortfolio()

        // Close modal after success
        setTimeout(() => {
          show.value = false
          sellSuccess.value = false
        }, 500)
      } catch (err) {
        console.error('Sale failed:', err)
        sellError.value = 'Sale failed. Please check your password and try again.'
      }
    }

    return {
      stocks,
      loading,
      error,
      sellModal,
      sellData,
      show,
      calculateReturnRate,
      getReturnRateClass,
      sellError,
      sellSuccess,
      showSellModal,
      handleSellStock,
    }
  },
}
</script>

<style scoped>
@keyframes modalFadeIn {
  from {
    transform: translateY(-20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes pFadeIn {
  from {
    transform: translateY(-10px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.portfolio-container {
  padding: 20px;
}

.stock-list {
  margin-top: 20px;
}

.empty-message {
  margin: 40px 0;
  text-align: center;
  color: #6c757d;
  font-size: 1.1rem;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #f2f2f2;
  font-weight: bold;
}

tr:hover {
  background-color: #f5f5f5;
}

p {
  margin: 10px;
  font-size: large;
  font-weight: bold;
}

h3 {
  color: #dc3545;
}

button {
  padding: 6px 12px;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.sell-btn {
  padding: 10px;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
}

button:hover {
  background-color: #c82333;
}

label {
  font-weight: 590;
  margin-bottom: 5px;
  font-size: 0.9rem;
  color: #333;
}

.form-group {
  margin: 10px;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

input {
  width: 80%;
  padding: 10px;
  border: 2px solid #ccc;
  border-radius: 8px;
  outline: none;
  transition:
    border-color 0.3s ease-in-out,
    box-shadow 0.3s ease-in-out;
}

input:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.3);
}

input[type='password'] {
  letter-spacing: 0.1em;
}

/* 수익률 색상 - 음수면 파란색, 양수면 빨간색 */
.price-up {
  color: #dc3545; /* 빨간색 (오름) */
  font-weight: bold;
}

.price-down {
  color: #007bff; /* 파란색 (내림) */
  font-weight: bold;
}

/* Modal styles */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
  position: relative;
}

.close {
  position: absolute;
  top: 10px;
  right: 15px;
  font-size: 24px;
  cursor: pointer;
}

.total-value {
  margin: 20px 0;
  font-weight: bold;
  font-size: 1.1rem;
  color: #f9a825;
}

.error-message {
  color: #dc3545;
  margin-top: 10px;
}

.success-message {
  color: #28a745;
  margin-top: 10px;
}
</style>
