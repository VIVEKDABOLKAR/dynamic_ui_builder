import apiClient from './apiClient'

export const getActionsByPageCode = async (pageCode) => {
  const response =
    await apiClient.get(`/api/admin/actions/page/${pageCode}`);
  return response.data;
};

export const createPageAction  = async (pageCode, payload) => {
  const response = await apiClient.post(`/api/admin/actions/page/${pageCode}`, payload);
  return response.data
}

// export const createComponentAction  = async (pageCode, payload) => {
//   const response = await apiClient.post(`/api/admin/actions/component/${pageCode}`, payload);
//   return response.data
// }

export const updatePageAction  = async (id, payload) => {
  const response = await apiClient.put(`/api/admin/actions/${id}`, payload);
  return response.data
}

export const deletePageAction  = async (id) => {
  const response = await apiClient.delete(`/api/admin/actions/${id}`);
  return response.data
}


// ── Component-level actions (new table: ui_component_action) ───────────────
 
export const getComponentActions = async (componentId) => {
  const response = await apiClient.get(`/api/admin/component-actions/component/${componentId}`);
  return response.data;
};
 
export const getComponentActionsByPage = async (pageCode) => {
  const response = await apiClient.get(`/api/admin/component-actions/page/${pageCode}`);
  return response.data;
};
 
export const createComponentAction = async (payload) => {
  const response = await apiClient.post(`/api/admin/component-actions`, payload);
  return response.data;
};
 
export const updateComponentAction = async (id, payload) => {
  const response = await apiClient.put(`/api/admin/component-actions/${id}`, payload);
  return response.data;
};
 
export const deleteComponentAction = async (id) => {
  const response = await apiClient.delete(`/api/admin/component-actions/${id}`);
  return response.data;
};
 