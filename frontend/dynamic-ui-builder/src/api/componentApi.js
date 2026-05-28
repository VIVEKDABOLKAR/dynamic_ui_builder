import axios from 'axios'

const componentClient = axios.create({
  baseURL: 'http://localhost:8080/api/components',
  headers: {
    'Content-Type': 'application/json',
  },
})

export const getComponentsByPage = async (pageCode) => {
  const response = await componentClient.get(`/page/${pageCode}`)
  return response.data
}

export const createComponent = async (payload) => {
  const response = await componentClient.post('', payload)
  return response.data
}
