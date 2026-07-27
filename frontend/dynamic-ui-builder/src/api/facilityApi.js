// src/api/facilityApi.js
import apiClient from './apiClient' // plain client, NOT adminClient — end users need this too

export const getFacilities = async () => {
  const response = await apiClient.get('/api/facilities')
  return response.data
}

export const getAccessibleFacilities = async () => {
  const response = await apiClient.get('/api/facilities/accessible')
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