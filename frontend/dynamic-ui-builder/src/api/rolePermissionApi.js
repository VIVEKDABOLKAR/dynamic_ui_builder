// src/api/rolePermissionApi.js
import adminClient from './adminClient' // admin-only, prefixes /api/admin — ROLE_ADMIN required

export const getRoles = async () => {
  const response = await adminClient.get('/roles')
  return response.data // [{ code, name, patterns: [...] }]
}

export const getRole = async (code) => {
  const response = await adminClient.get(`/roles/${encodeURIComponent(code)}`)
  return response.data
}

export const updateRolePermissions = async (code, patterns) => {
  const response = await adminClient.put(`/roles/${encodeURIComponent(code)}/permissions`, { patterns })
  return response.data
}

export const createRole = async ({ code, name, description }) => {
  const response = await adminClient.post('/roles', { code, name, description })
  return response.data
}
