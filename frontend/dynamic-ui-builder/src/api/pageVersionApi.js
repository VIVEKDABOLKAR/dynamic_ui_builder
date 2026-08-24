import adminClient from './adminClient'

export const getVersionHistory = async (pageCode) => {
  const response = await adminClient.get(`/pages/${pageCode}/versions`)
  return response.data
}

export const createVersion = async (pageCode, changeSummary) => {
  const response = await adminClient.post(`/pages/${pageCode}/versions`, { changeSummary })
  return response.data
}

export const getVersion = async (pageCode, versionNumber) => {
  const response = await adminClient.get(`/pages/${pageCode}/versions/${versionNumber}`)
  return response.data
}

export const restoreVersion = async (pageCode, versionNumber) => {
  const response = await adminClient.post(`/pages/${pageCode}/versions/${versionNumber}/restore`)
  return response.data
}
