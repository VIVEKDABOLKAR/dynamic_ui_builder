import apiClient from './apiClient'

export const getComponentsByPage = async (pageCode) => {
  const response = await apiClient.get(`/api/components/page/${pageCode}`)
  return response.data
}

export const createComponent = async (payload) => {
  const response = await apiClient.post('/api/components', payload)
  return response.data
}

export const updateComponent = async (id, payload) => {
  const response = await apiClient.put(`/api/components/${id}`, payload)
  return response.data
}

export const deleteComponent = async (id) => {
  const response = await apiClient.delete(`/api/components/${id}`)
  return response.data
}
