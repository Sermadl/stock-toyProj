// components/MarketView.vue
<template>
  <div class="market-container">
    <h2>Stock Market</h2>

    <div class="loading-message" v-if="loading">Loading stocks...</div>
    <div class="error-message" v-if="error">{{ error }}</div>

    <div class="stock-list" v-if="!loading && !error">
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Code</th>
            <th>Price</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="stock in stocks" :key="stock.code">
            <td>{{ stock.name }}</td>
            <td>{{ stock.code }}</td>
            <td>{{ stock.price.toLocaleString() }}</td>
            <td>
              <button @click="showPurchaseModal(stock)">Buy</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Purchase Modal -->
    <div class="modal" v-if="show">
      <div class="modal-content">
        <span class="close" @click="show = false">&times;</span>
        <h3>Purchase {{ purchaseModal.name }}</h3>
        <p>Current Price: {{ purchaseModal.price.toLocaleString() }}</p>

        <form @submit.prevent="handlePurchase">
          <div class="form-group">
            <label for="quantity">Quantity</label>
            <input
              id="quantity"
              v-model.number="purchaseData.quantity"
              type="number"
              min="1"
              required
            />
          </div>

          <div class="form-group">
            <label for="password">Confirm Password</label>
            <input id="password" v-model="purchaseData.password" type="password" required />
          </div>

          <div class="total-cost">
            Total Cost: {{ (purchaseData.quantity * purchaseModal.price).toLocaleString() }}
          </div>

          <button type="submit" class="submit-btn purchase-btn">Confirm Purchase</button>
          <div v-if="purchaseError" class="error-message">{{ purchaseError }}</div>
          <div v-if="purchaseSuccess" class="success-message">Purchase successful!</div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { purchaseStock } from '@/api/market'
import { getAllStocks } from '@/api/stock'

export default {
  name: 'MarketView',
  setup() {
    const stocks = ref([])
    const loading = ref(true)
    const error = ref('')

    const purchaseModal = ref({
      name: '',
      code: '',
      price: 0,
    })

    const purchaseData = ref({
      password: '',
      stockId: 0,
      quantity: 0,
    })

    const purchaseError = ref('')
    const purchaseSuccess = ref(false)
    const show = ref(false)

    onMounted(async () => {
      try {
        loading.value = true
        stocks.value = await getAllStocks()
      } catch (err) {
        error.value = 'Failed to load stocks. Please try again later.'
        console.error('Error loading stocks:', err)
      } finally {
        loading.value = false
      }
    })

    const showPurchaseModal = (stock) => {
      show.value = true
      purchaseModal.value = {
        name: stock.name,
        code: stock.code,
        price: stock.price,
      }
      purchaseData.value = {
        password: '',
        stockId: stock.id,
        quantity: 0,
      }

      console.log('stock id: ', stock.id)
      console.log('token: ', document.cookie)
    }

    const handlePurchase = async () => {
      try {
        purchaseError.value = ''
        purchaseSuccess.value = false

        console.log('purchaseData: ', purchaseData.value)

        await purchaseStock(purchaseData.value)

        purchaseSuccess.value = true

        // Close modal after success
        setTimeout(() => {
          show.value = false
          purchaseSuccess.value = false
        }, 500)
      } catch (err) {
        console.error('Purchase failed:', err)
        purchaseError.value = 'Purchase failed. Please check your password and balance.'
      }
    }

    return {
      stocks,
      loading,
      error,
      purchaseModal,
      purchaseData,
      showPurchaseModal,
      show,
      purchaseError,
      purchaseSuccess,
      handlePurchase,
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

.market-container {
  padding: 20px;
}

.stock-list {
  margin-top: 20px;
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
  color: #28a745;
}

button {
  padding: 6px 12px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.purchase-btn {
  padding: 10px;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
}

button:hover {
  background-color: #218838;
}

.form-group {
  margin: 10px;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

label {
  font-weight: 590;
  margin-bottom: 5px;
  font-size: 0.9rem;
  color: #333;
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

input.error {
  border-color: #dc3545;
  background-color: #f8d7da;
}

input[type='password'] {
  letter-spacing: 0.1em;
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
  border-radius: 10px;
  width: 400px;
  max-width: 90%;
  position: relative;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  animation: modalFadeIn 0.3s ease-in-out;
}

.close {
  position: absolute;
  top: 10px;
  right: 15px;
  font-size: 24px;
  cursor: pointer;
}

.total-cost {
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
