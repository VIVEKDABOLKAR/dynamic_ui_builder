// lookupApi.js

import apiClient from "./apiClient"

// // Generic admin-classification lookups (MODULE_CODE, CATEGORY_CODE,
// // LAYOUT_CODE, PARENT_MENU, ...) — distinct from the component-scoped
// // lookups used for dynamic form dropdowns rendered on live pages.
export const getLookupsByType = async (lookupType) => {
  const response = await apiClient.get(`/api/ui/lookups/type/${lookupType}`)
  return response.data
}

export const getLookupsMasters = async () => {
  const response = await apiClient.get("/api/ui/lookup-masters");
  return response.data;
}

export const getLookupValuesByMaster = async (masterId) => {
  const response = await apiClient.get(`/api/ui/lookups/master/${masterId}`);
  return response.data;
};

export const createLookupMaster = async (data) => {
  const response = await apiClient.post("/api/ui/lookup-masters", data);
  return response.data;
};

export const updateLookupMaster = async (id, data) => {
  const response = await apiClient.put(`/api/ui/lookup-masters/${id}`, data);
  return response.data;
};

export const deleteLookupMaster = async (id) => {
  return apiClient.delete(`/api/ui/lookup-masters/${id}`);
};

export const createLookup = async (data) => {
  const response = await apiClient.post("/api/ui/lookups", data);
  return response.data;
};

export const updateLookup = async (id, data) => {
  const response = await apiClient.put(`/api/ui/lookups/${id}`, data);
  return response.data;
};

export const deleteLookup = async (id) => {
  return apiClient.delete(`/api/ui/lookups/${id}`);
};