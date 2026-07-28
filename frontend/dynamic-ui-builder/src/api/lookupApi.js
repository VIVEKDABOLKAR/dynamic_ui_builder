// import apiClient from "./apiClient"

// // Generic admin-classification lookups (MODULE_CODE, CATEGORY_CODE,
// // LAYOUT_CODE, PARENT_MENU, ...) — distinct from the component-scoped
// // lookups used for dynamic form dropdowns rendered on live pages.
// export const getLookupsByType = async (lookupType) => {
//   const response = await apiClient.get(`/api/ui/lookups/type/${lookupType}`)
//   return response.data
// }


// lookupApi.js

// Temporary hardcoded data for testing
const mockLookups = {
  MODULE_CODE: [
    { lookupValue: "USER", displayValue: "User Management" },
    { lookupValue: "ADMIN", displayValue: "Administration" },
    { lookupValue: "REPORT", displayValue: "Reports" },
  ],
  CATEGORY_CODE: [
    { lookupValue: "MASTER", displayValue: "Master Data" },
    { lookupValue: "TRANSACTION", displayValue: "Transaction" },
    { lookupValue: "SETTINGS", displayValue: "Settings" },
  ],
  LAYOUT_CODE: [
    { lookupValue: "GRID", displayValue: "Grid Layout" },
    { lookupValue: "LIST", displayValue: "List Layout" },
    { lookupValue: "CARD", displayValue: "Card Layout" },
  ],
}

export const getLookupsByType = async (lookupType) => {
  // Simulate API delay
  await new Promise((resolve) => setTimeout(resolve, 500))

  return mockLookups[lookupType] || []

  // Uncomment when ready to use the real API
  // const response = await apiClient.get(`/api/ui/lookups/type/${lookupType}`)
  // return response.data
}