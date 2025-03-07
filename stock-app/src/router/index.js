import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import MarketView from '@/views/MarketView.vue'
import LoginView from '@/views/LoginView.vue'
import PortfolioView from '@/views/PortFolioView.vue'
import StocksView from '@/views/StocksView.vue'
import StockDetailView from '@/views/StockDetailView.vue'
import AccountView from '@/views/AccountView.vue'

const routes = [
  { path: '/', component: HomeView },
  { path: '/market', component: MarketView },
  { path: '/login', component: LoginView },
  { path: '/portFolio', component: PortfolioView },
  { path: '/account', component: AccountView },
  { path: '/stocks', component: StocksView },
  { path: '/stocks/:id', component: StockDetailView, props: true },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
