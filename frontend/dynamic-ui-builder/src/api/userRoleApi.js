import adminClient from './adminClient'

export const getUsers = async () => {
  const response = await adminClient.get('/users')
  return response.data
}

export const updateUserRole = async (userId, role) => {
  const response = await adminClient.put(`/users/${userId}/role`, { role })
  return response.data
}

export const getRoles = async () => {
    const response = await adminClient.get('/roles')
    console.log(response);
    return response.data
}