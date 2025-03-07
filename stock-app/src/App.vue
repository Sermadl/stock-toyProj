<template>
  <div class="app-container">
    <header>
      <h1>Stock Market</h1>
      <nav v-if="isLoggedIn">
        <router-link to="/market">Market</router-link>
        <router-link to="/portfolio">My Portfolio</router-link>
        <router-link to="/account">Account</router-link>
        <button @click="logout" class="logout-btn">Logout</button>
      </nav>
    </header>

    <main>
      <router-view @login-success="handleLoginSuccess" />
    </main>

    <footer>
      <p>Stock Market Simulator &copy; 2025</p>
    </footer>
  </div>
</template>

<script>
import { ref, watchEffect, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { logout } from '@/api/auth'

export default {
  setup() {
    const router = useRouter()
    const isLoggedIn = ref(!!localStorage.getItem('isLoggedIn'))

    watchEffect(() => {
      isLoggedIn.value = !!localStorage.getItem('isLoggedIn')
    })

    onMounted(() => {
      if (!isLoggedIn.value) {
        router.push('/login') // 로그인되지 않은 경우 로그인 페이지로 이동
      } else {
        router.push('/market') // 로그인되어 있는 경우 `MarketView`로 이동
      }
    })

    const handleLoginSuccess = () => {
      localStorage.setItem('isLoggedIn', 'true')
      isLoggedIn.value = true
      router.push('/market') // 로그인 후 `MarketView`로 이동
    }

    const handleLogout = async () => {
      try {
        await logout()
        localStorage.removeItem('isLoggedIn')
        isLoggedIn.value = false
        router.push('/login') // 로그아웃 후 로그인 페이지로 이동
      } catch (error) {
        console.error('Logout failed:', error)
      }
    }

    return {
      isLoggedIn,
      onMounted,
      handleLoginSuccess,
      logout: handleLogout,
    }
  },
}
</script>

<style scoped>
.app-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

nav {
  display: flex;
  gap: 10px;
}

nav a {
  text-decoration: none;
  padding: 8px 15px;
  background-color: #f8f9fa;
  border: 1px solid #ddd;
  border-radius: 4px;
  color: black;
  transition: 0.3s;
}

nav a:hover {
  background-color: #e9ecef;
}

nav a.router-link-exact-active {
  background-color: #007bff;
  color: white;
}

.logout-btn {
  background-color: #f8d7da;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
}

footer {
  margin-top: 50px;
  text-align: center;
  color: #6c757d;
  font-size: 0.9rem;
}
</style>
