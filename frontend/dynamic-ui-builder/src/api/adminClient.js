import apiClient from './apiClient'

const withAdminPrefix = (url) => `/api/admin${url}`

const adminClient = {
  get: (url, config) => apiClient.get(withAdminPrefix(url), config),
  post: (url, data, config) => apiClient.post(withAdminPrefix(url), data, config),
  put: (url, data, config) => apiClient.put(withAdminPrefix(url), data, config),
  delete: (url, config) => apiClient.delete(withAdminPrefix(url), config),
}

export default adminClient