// components/LoginRegister.vue
<template>
  <div class="auth-container">
    <div class="tabs">
      <button :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">Login</button>
      <button :class="{ active: activeTab === 'register' }" @click="activeTab = 'register'">
        Register
      </button>
    </div>

    <div class="form-container">
      <!-- Login Form -->
      <form v-if="activeTab === 'login'" @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="login-id">Player ID</label>
          <input id="login-id" v-model="loginForm.playerId" type="text" required />
        </div>

        <div class="form-group">
          <label for="login-password">Password</label>
          <input id="login-password" v-model="loginForm.password" type="password" required />
        </div>

        <button type="submit" class="submit-btn">Login</button>

        <p v-if="loginError" class="error-message">{{ loginError }}</p>
      </form>

      <!-- Registration Form -->
      <form v-else @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="register-id">Player ID</label>
          <input id="register-id" v-model="registerForm.playerId" type="text" required />
        </div>

        <div class="form-group">
          <label for="register-name">Name</label>
          <input id="register-name" v-model="registerForm.name" type="text" required />
        </div>

        <div class="form-group">
          <label for="register-password">Password</label>
          <input id="register-password" v-model="registerForm.password" type="password" required />
        </div>

        <button type="submit" class="submit-btn">Register</button>

        <p v-if="registerSuccess" class="success-message">
          Registration successful! You can now log in.
        </p>
        <p v-if="registerError" class="error-message">{{ registerError }}</p>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '@/api/auth'

export default {
  name: 'LoginRegister',
  emits: ['login-success'],
  setup(props, { emit }) {
    const activeTab = ref('login')
    const router = useRouter()

    const loginForm = ref({
      playerId: '',
      password: '',
    })

    const registerForm = ref({
      playerId: '',
      password: '',
      name: '',
    })

    const loginError = ref('')
    const registerError = ref('')
    const registerSuccess = ref(false)

    const handleLogin = async () => {
      try {
        loginError.value = ''
        await login(loginForm.value)
        localStorage.setItem('isLoggedIn', 'true')
        localStorage.setItem('playerId', loginForm.value.playerId)
        emit('login-success')
        router.push('/market')
      } catch (error) {
        console.error('Login failed:', error)
        loginError.value = 'Login failed. Please check your credentials.'
      }
    }

    const handleRegister = async () => {
      try {
        registerError.value = ''
        registerSuccess.value = false
        await register(registerForm.value)
        registerSuccess.value = true
        // Reset form
        registerForm.value = {
          playerId: '',
          password: '',
          name: '',
        }
        // Switch to login tab after successful registration
        setTimeout(() => {
          activeTab.value = 'login'
        }, 2000)
      } catch (error) {
        console.error('Registration failed:', error)
        registerError.value = 'Registration failed. Please try again.'
      }
    }

    return {
      activeTab,
      loginForm,
      registerForm,
      loginError,
      registerError,
      registerSuccess,
      handleLogin,
      handleRegister,
    }
  },
}
</script>

<style scoped>
.auth-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.tabs {
  display: flex;
  margin-bottom: 20px;
}

.tabs button {
  flex: 1;
  padding: 10px;
  background-color: #eee;
  border: none;
  cursor: pointer;
}

.tabs button.active {
  background-color: #fff;
  border-bottom: 2px solid #007bff;
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
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.submit-btn:hover {
  background-color: #0069d9;
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
