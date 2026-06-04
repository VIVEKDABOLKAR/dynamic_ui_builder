import apiClient from './apiClient'

export const getActionsByPageCode = async (pageCode) => {
  const response =
    await apiClient.get(`/api/admin/actions/page/${pageCode}`);
  return response.data;
};

export const createPageAction  = async (pageCode, payload) => {
  const response = await apiClient.post(`/api/admin/actions/${pageCode}`, payload);
  return response.data
}

export const createComponentAction  = async (pageCode, payload) => {
  const response = await apiClient.post(`/api/admin/actions/component/${pageCode}`, payload);
  return response.data
}

export const updatePageAction  = async (id, payload) => {
  const response = await apiClient.put(`/api/admin/actions/${id}`, payload);
  return response.data
}

export const deletePageAction  = async (id) => {
  const response = await apiClient.delete(`/api/admin/actions/${id}`);
  return response.data
}
