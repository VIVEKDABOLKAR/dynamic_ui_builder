// globalUiApi.js
import apiClient from './apiClient'   // <-- the plain client with baseURL: 'http://localhost:8080', NOT adminClient

// Stored shape (this is exactly what getNavbar returns):
// {
//   style: { backgroundColor, textColor, height, borderStyle },
//   components: [
//     { id: "logo",       type: "logo",         visible, order: 1, position: "left",   props: { imageUrl } },
//     { id: "facilities", type: "facilityList",  visible, order: 2, position: "center", props: {} },
//     { id: "profile",    type: "profile",       visible, order: 3, position: "right",  props: {} },
//   ]
// }

export const buildNavbarPayload = (facilityId, facilityName, config) => ({
  facilityId,
  facilityName,
  type: 'NAVBAR',

  config: {
    style: {
      backgroundColor: config.backgroundColor,
      textColor: config.textColor,
      height: config.height,
      borderStyle: config.borderStyle,
    },

    components: [
      {
        id: 'logo',
        type: 'logo',
        visible: config.showLogo ?? true,
        order: 1,
        position: 'left',
        props: { imageUrl: config.logoUrl || '' },
      },
      {
        id: 'facilities',
        type: 'facilityList',
        visible: config.showFacilities ?? true,
        order: 2,
        position: 'center',
        props: {}, // facility data is fetched live, not stored here
      },
      {
        id: 'profile',
        type: 'profile',
        visible: config.showProfile,
        order: 3,
        position: 'right',
        props: {},
      },
    ],
  },
})

export const parseNavbarPayload = (payload) => {
  if (!payload) return null

  const find = (type) => payload.components?.find((c) => c.type === type)
  const logo = find('logo')
  const facilityList = find('facilityList')
  const profile = find('profile')

  return {
    backgroundColor: payload.style?.backgroundColor || '',
    textColor: payload.style?.textColor || '',
    height: payload.style?.height || '',
    borderStyle: payload.style?.borderStyle || '',
    showLogo: logo?.visible ?? true,
    logoUrl: logo?.props?.imageUrl || '',
    showFacilities: facilityList?.visible ?? true,
    showProfile: profile?.visible ?? true,
  }
}

export const getNavbarStyle = async () => {
  const response = await apiClient.get('/api/global-ui/navbar/style')
  return response.data // { backgroundColor, textColor, height, borderStyle }
}

export const saveNavbarStyle = async (style) => {
  await apiClient.post('/api/global-ui/navbar/style', style)
}

export const getNavbarComponents = async (facilityId) => {
  const response = await apiClient.get(`/api/global-ui/navbar/components/${facilityId}`)
  return response.data // { showLogo, logoUrl, showProfile, showFacilities }
}

export const saveNavbarComponents = async (facilityId, components) => {
  await apiClient.post(`/api/global-ui/navbar/components/${facilityId}`, components)
}