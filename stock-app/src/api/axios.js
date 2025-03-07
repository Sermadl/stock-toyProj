import axios from 'axios'

const apiClient = axios.create({
  baseURL: 'http://localhost:8080', // 스프링 서버 주소
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
})

export default apiClient
