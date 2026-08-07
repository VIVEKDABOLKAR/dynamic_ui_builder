// src/api/workflowApi.js
import adminClient from './adminClient' // admin-only, prefixes /api/admin — ROLE_ADMIN required

//---------------------------
// Workflow Steps (master data)
//---------------------------

export const getWorkflowSteps = async () => {
  const response = await adminClient.get('/workflow-steps')
  return response.data
}

export const createWorkflowStep = async (payload) => {
  const response = await adminClient.post('/workflow-steps', payload)
  return response.data
}

export const updateWorkflowStep = async (id, payload) => {
  const response = await adminClient.put(`/workflow-steps/${id}`, payload)
  return response.data
}

export const deleteWorkflowStep = async (id) => {
  await adminClient.delete(`/workflow-steps/${id}`)
}

//---------------------------
// Workflow Configuration (order + active steps)
//---------------------------

export const getWorkflowConfigurations = async () => {
  const response = await adminClient.get('/workflow-configurations')
  return response.data
}

export const getWorkflowConfigurationsList = async () => {
  const response = await adminClient.get('/workflow-configurations/list')
  return response.data
}

export const createWorkflowConfiguration = async (payload) => {
  const response = await adminClient.post('/workflow-configurations', payload)
  return response.data
}

export const updateWorkflowConfiguration = async (id, payload) => {
  const response = await adminClient.put(`/workflow-configurations/${id}`, payload)
  return response.data
}

export const deleteWorkflowConfiguration = async (id) => {
  await adminClient.delete(`/workflow-configurations/${id}`)
}
