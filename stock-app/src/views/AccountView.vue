// components/AccountView.vue
<template>
  <div class="account-container">
    <h2>My Account</h2>

    <div class="loading-message" v-if="loading">Loading account information...</div>
    <div class="error-message" v-if="error">{{ error }}</div>

    <div class="account-details" v-if="!loading && !error && playerInfo">
      <div class="info-card">
        <h3>Account Information</h3>
        <div class="info-row">
          <span class="label">Name:</span>
          <span class="value">{{ playerInfo.name }}</span>
        </div>
        <div class="info-row">
          <span class="label">Balance:</span>
          <span class="value">{{ playerInfo.playerMoney.toLocaleString() }}</span>
        </div>
      </div>

      <div v-if="!isAdmin" class="action-cards">
        <div class="card">
          <h3>Deposit Funds</h3>
          <form @submit.prevent="handleDeposit">
            <div class="form-group">
              <label for="deposit-amount">Amount</label>
              <input
                id="deposit-amount"
                v-model.number="depositForm.money"
                type="number"
                min="1"
                required
              />
            </div>

            <div class="form-group">
              <label for="deposit-password">Confirm Password</label>
              <input
                id="deposit-password"
                v-model="depositForm.password"
                type="password"
                required
              />
            </div>

            <button
              type="submit"
              class="submit-btn deposit-btn"
              :class="{ 'button-active': isDepositFormValid }"
            >
              Deposit
            </button>
            <div v-if="depositError" class="error-message">{{ depositError }}</div>
            <div v-if="depositSuccess" class="success-message">Deposit successful!</div>
          </form>
        </div>

        <div class="card">
          <h3>Withdraw Funds</h3>
          <form @submit.prevent="handleWithdraw">
            <div class="form-group">
              <label for="withdraw-amount">Amount</label>
              <input
                id="withdraw-amount"
                v-model.number="withdrawForm.money"
                type="number"
                min="1"
                :max="playerInfo.playerMoney"
                required
              />
            </div>

            <div class="form-group">
              <label for="withdraw-password">Confirm Password</label>
              <input
                id="withdraw-password"
                v-model="withdrawForm.password"
                type="password"
                required
              />
            </div>

            <button
              type="submit"
              class="submit-btn withdraw-btn"
              :class="{ 'button-active': isWithdrawFormValid }"
            >
              Withdraw
            </button>
            <div v-if="withdrawError" class="error-message">{{ withdrawError }}</div>
            <div v-if="withdrawSuccess" class="success-message">Withdrawal successful!</div>
          </form>
        </div>
      </div>

      <!-- 관리자 전용 기능 -->
      <div v-if="isAdmin" class="card">
        <h3>Admin Menu</h3>
        <h3>Add Stock</h3>
        <form @submit.prevent="handleAddStock">
          <div class="form-group">
            <label for="add-stock-name">Name</label>
            <input id="add-stock-name" v-model="addStockForm.name" type="text" required />
          </div>
          <div class="form-group">
            <label for="add-stock-code">Code</label>
            <input id="add-stock-code" v-model="addStockForm.code" type="text" min="1" required />
          </div>
          <div class="form-group">
            <label for="add-stock-price">Price</label>
            <input
              id="add-stock-price"
              v-model.number="addStockForm.price"
              type="number"
              required
            />
          </div>
          <div class="form-group">
            <label for="add-stock-description">Description</label>
            <input
              id="add-stock-description"
              v-model="addStockForm.description"
              type="text"
              required
            />
          </div>

          <button
            type="submit"
            class="submit-btn add-stock-btn"
            :class="{ 'button-active': isAddStockFormValid }"
          >
            Add Stock
          </button>
          <div v-if="addError" class="error-message">{{ addError }}</div>
          <div v-if="addSuccess" class="success-message">Stock added successfully!</div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { getPlayerInfo, deposit, withdraw } from '@/api/player'
import { addStock } from '@/api/stock'
import { useAuthStore } from '@/store/authStore'

export default {
  name: 'AccountView',
  setup() {
    const playerInfo = ref(null)
    const loading = ref(true)
    const error = ref('')
    const authStore = useAuthStore()

    const depositForm = ref({
      money: 1000,
      password: '',
    })

    const withdrawForm = ref({
      money: 1000,
      password: '',
    })

    const addStockForm = ref({
      name: '',
      code: '',
      price: 0,
      description: '',
    })

    const addError = ref('')
    const depositError = ref('')
    const withdrawError = ref('')
    const addSuccess = ref(false)
    const depositSuccess = ref(false)
    const withdrawSuccess = ref(false)

    const isDepositFormValid = computed(() => {
      return depositForm.value.money > 0 && depositForm.value.password.trim() !== ''
    })

    const isWithdrawFormValid = computed(() => {
      return (
        withdrawForm.value.money > 0 &&
        withdrawForm.value.password.trim() !== '' &&
        playerInfo.value &&
        withdrawForm.value.money <= playerInfo.value.playerMoney
      )
    })

    const isAddStockFormValid = computed(() => {
      return (
        addStockForm.value.name.trim() !== '' &&
        addStockForm.value.code.trim() !== '' &&
        addStockForm.value.price > 0 &&
        addStockForm.value.description.trim() !== ''
      )
    })

    const loadAccountInfo = async () => {
      try {
        loading.value = true
        error.value = ''
        playerInfo.value = await getPlayerInfo()
        authStore.fetchUserInfo()
      } catch (err) {
        console.error('Error loading account info:', err)
        error.value = 'Failed to load your account information. Please try again later.'
      } finally {
        loading.value = false
      }
    }

    onMounted(loadAccountInfo)

    const handleDeposit = async () => {
      try {
        depositError.value = ''
        depositSuccess.value = false

        await deposit(depositForm.value)

        depositSuccess.value = true
        depositForm.value.money = 1000
        depositForm.value.password = ''

        // Reload account info to update balance
        await loadAccountInfo()
      } catch (err) {
        console.error('Deposit failed:', err)
        depositForm.value.error = 'Deposit failed. Please check your password and try again.'
      }
    }

    const handleWithdraw = async () => {
      try {
        withdrawError.value = ''
        withdrawSuccess.value = false

        await withdraw(withdrawForm.value)

        withdrawSuccess.value = true
        withdrawForm.value.money = 1000
        withdrawForm.value.password = ''

        // Reload account info to update balance
        await loadAccountInfo()
      } catch (err) {
        console.error('Withdrawal failed:', err)
        withdrawForm.value.error = 'Withdrawal failed. Please check your password and balance.'
      }
    }

    const handleAddStock = async () => {
      try {
        addError.value = ''
        addSuccess.value = false

        console.log('Sending stock data:', JSON.stringify(addStockForm.value))

        // Call API to add stock
        await addStock(addStockForm.value)

        addStockForm.value = true
        // Reset form
        addStockForm.value = {
          name: '',
          code: '',
          price: 0,
          description: '',
        }

        // Reload account info to update stock list
        await loadAccountInfo()
      } catch (err) {
        console.error('Add stock failed:', err)
        addStockForm.value.error = 'Failed to add stock. Please check your input and try again.'
      }
    }

    return {
      playerInfo,
      loading,
      error,
      depositForm,
      withdrawForm,
      addStockForm,
      handleDeposit,
      handleWithdraw,
      handleAddStock,
      isAdmin: computed(() => authStore.isAdmin),
      isDepositFormValid,
      isWithdrawFormValid,
      isAddStockFormValid,
    }
  },
}
</script>

<style scoped>
.account-container {
  padding: 20px;
}

.account-details {
  margin-top: 20px;
}

.info-card {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
}

.info-row {
  margin: 10px 0;
  display: flex;
}

.label {
  font-weight: bold;
  width: 100px;
}

.value {
  flex: 1;
}

.action-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.card {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.submit-btn {
  background-color: #b9b9b9;
  width: 100%;
  padding: 10px;
  color: white;
  border: none;
  border-radius: 4px;
}

.button-active {
  background-color: #007bff; /* Active button color */
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.3);
}
</style>
