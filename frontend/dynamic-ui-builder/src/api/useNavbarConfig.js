import { useEffect, useState } from 'react'
import apiClient from './apiClient' // plain client, not adminClient — see fix from earlier

export function useNavbarConfig(facilityId) {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!facilityId) return
    let mounted = true
    setLoading(true)
    apiClient.get(`/api/global-ui/navbar/${facilityId}`)
      .then((res) => mounted && setData(res.data))
      .catch(() => mounted && setData(null)) // 404 -> fall back to defaults
      .finally(() => mounted && setLoading(false))
    return () => { mounted = false }
  }, [facilityId])

  return { data, loading }
}