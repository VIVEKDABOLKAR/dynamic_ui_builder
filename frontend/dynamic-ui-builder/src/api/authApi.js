import axios from 'axios'

const BASE = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080') + '/api/auth'

export const signup = async (username, password) => {
  const { data } = await axios.post(`${BASE}/register`, { username, password, role:   'ROLE_VIEWER' })
  localStorage.setItem('token', data.token)
  localStorage.setItem('role', data.role)
  localStorage.setItem('username', username)
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
    // Decode the payload (middle part) and check exp
    const payload = JSON.parse(atob(token.split('.')[1]))
    const isExpired = payload.exp * 1000 < Date.now()
    if (isExpired) {
      logout()       // clean up expired token immediately
      return false
    }
    return true
  } catch {
    logout()
    return false
  }
}