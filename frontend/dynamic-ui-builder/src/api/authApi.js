import axios from 'axios'

const BASE = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080') + '/api/auth'

export const signup = async (username, password, facilityIds) => {
  const { data } = await axios.post(`${BASE}/register`, {
    username,
    password,
    role: 'ROLE_VIEWER',
    facilityIds,
  })
  // No auto-login here — account requires admin approval for facility access,
  // so redirect to login instead of storing a token immediately.
  return data
}

export const login = async (username, password) => {
  const { data } = await axios.post(`${BASE}/login`, { username, password })
  localStorage.setItem('token', data.token)
  localStorage.setItem('role', data.role)
  localStorage.setItem('username', username)
  return data
}

export const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  localStorage.removeItem('username')
}

export const getRole = () => localStorage.getItem('role')

export const getUsername = () => localStorage.getItem('username')

export const isLoggedIn = () => {
  const token = localStorage.getItem('token')
  if (!token) return false
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    const isExpired = payload.exp * 1000 < Date.now()
    if (isExpired) {
      logout()
      return false
    }
    return true
  } catch {
    logout()
    return false
  }
}