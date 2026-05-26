import axios from 'axios'

const uiClient = axios.create({
  baseURL: 'http://localhost:8080/api/ui/pages',
})

export const getUiPageByCode = async (pageCode) => {
  const response = await uiClient.get(`/${pageCode}`)
  return response.data
}