// src/api/facilityApi.js
import apiClient from './apiClient' // plain client, NOT adminClient — end users need this too

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