import apiClient from './apiClient'

export const getUiPageByCode = async (pageCode) => {
  const response = await apiClient.get(`/api/ui/pages/${pageCode}`)
  return response.data
}

// New — fetches all active pages for the home dashboard
export const getAllUiPages = async () => {
  const response = await apiClient.get('/api/ui/pages')
  console.log("gdf")
  return response.data
}