// src/api/facilityApi.js
import apiClient from './apiClient' // plain client, NOT adminClient — end users need this too
import adminClient from './adminClient' // admin-only client, prefixes /api/admin — ROLE_ADMIN required

export const getFacilities = async () => {
  const response = await apiClient.get('/api/facilities')
  return response.data
}

export const getAccessibleFacilities = async () => {
  const response = await apiClient.get('/api/facilities/accessible')
  console.log(response)
  return response.data
}

export const getPendingFacilityRequests = async () => {
  const response = await apiClient.get('/api/admin/facility-access/pending')
  return response.data
}

export const approveFacilityRequest = async (id) => {
  await apiClient.post(`/api/admin/facility-access/${id}/approve`)
}

export const rejectFacilityRequest = async (id) => {
  await apiClient.post(`/api/admin/facility-access/${id}/reject`)
}

export const changeFacilityRequest = async (id) => {
  const response = await apiClient.post(`/api/facilities/change-facility?facilityId=${encodeURIComponent(id)}`);
  const {data} = response;
  localStorage.setItem('token', data.token)
  localStorage.setItem('facilityId', data.facilityId)
  return data;
}

//---------------------------
// Admin CRUD — /api/admin/facilities (ROLE_ADMIN only)
//---------------------------

export const createFacility = async (payload) => {
  const response = await adminClient.post('/facilities', payload)
  return response.data
}

export const updateFacility = async (id, payload) => {
  const response = await adminClient.put(`/facilities/${encodeURIComponent(id)}`, payload)
  return response.data
}

export const deleteFacility = async (id) => {
  await adminClient.delete(`/facilities/${encodeURIComponent(id)}`)
}