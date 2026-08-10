// src/api/routeAccessApi.js
import adminClient from './adminClient' // admin-only, prefixes /api/admin — ROLE_ADMIN required

// Route master list — reuses the existing UIRoute admin endpoint rather
// than a separate RouteMaster table.
export const getRoutes = async () => {
  const response = await adminClient.get('/routes')
  return response.data
}

export const getFacilityRouteAccess = async (facilityId) => {
  const response = await adminClient.get(`/facilities/${encodeURIComponent(facilityId)}/route-access`)
  return response.data
}

export const updateFacilityRouteAccess = async (facilityId, routeIds) => {
  const response = await adminClient.put(`/facilities/${encodeURIComponent(facilityId)}/route-access`, { routeIds })
  console.log(response.data)
  return response.data
}

// Global — no stored "Global" facility; GET returns the intersection of
// what every facility currently has granted, PUT bulk-writes to all of them.
export const getGlobalRouteAccess = async () => {
  const response = await adminClient.get('/route-access/global')
  return response.data
}

export const updateGlobalRouteAccess = async (routeIds) => {
  const response = await adminClient.put('/route-access/global', { routeIds })
  return response.data
}
