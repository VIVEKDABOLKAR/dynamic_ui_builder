import adminClient from './adminClient'

export const createPage = async (payload) => {
  const response = await adminClient.post('/pages', payload)
  return response.data
}

export const getAllPages = async () => {
  const response = await adminClient.get('/pages')
  return response.data
}