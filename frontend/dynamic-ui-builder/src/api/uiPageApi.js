import apiClient from './apiClient'

export const getUiPageByCode = async (pageCode) => {
  const response = await apiClient.get(`/api/ui/pages/${pageCode}`)
  return response.data
}