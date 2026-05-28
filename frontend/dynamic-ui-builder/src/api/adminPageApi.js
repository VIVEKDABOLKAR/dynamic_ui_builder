import adminClient from './adminClient'

export const createPage = async (payload) => {
  const response = await adminClient.post('/pages', payload)
  return response.data
}

export const getAllPages = async () => {
  const response = await adminClient.get('/pages')
  return response.data
}

export const getPageByCode = async (pageCode) => {
  const response = await adminClient.get(`/pages/${pageCode}`)
  return response.data
}

export const updatePage = async (pageCode, payload) => {
  const response = await adminClient.put(`/pages/${pageCode}`, payload)
  return response.data
}
