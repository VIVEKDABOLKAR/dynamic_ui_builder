import axios from 'axios'
import { toast } from 'react-toastify'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Attach token on every request
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// On 401 — clear storage and redirect to login
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
);

apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  
  (error) => {
    const message =
      error.response?.data?.message ||
      error.message ||
      "Something went wrong";

    toast.error(message);

    return Promise.reject(error);
  }
);

export default apiClient