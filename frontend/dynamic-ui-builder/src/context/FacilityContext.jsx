// src/context/FacilityContext.jsx
import { createContext, useContext, useEffect, useState } from 'react'
import { getAccessibleFacilities } from '../api/facilityApi'

const FacilityContext = createContext(null)

export function FacilityProvider({ children }) {
  const [facilities, setFacilities] = useState([])
  const [currentFacilityId, setCurrentFacilityId] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let mounted = true
    getAccessibleFacilities()
      .then((data) => {
        if (!mounted) return
        setFacilities(data || [])
        if (data?.length) setCurrentFacilityId(data[0].id)
      })
      .catch(() => mounted && setFacilities([]))
      .finally(() => mounted && setLoading(false))
    return () => { mounted = false }
  }, [])

  const currentFacility = facilities.find((f) => f.id === currentFacilityId) || null

  return (
    <FacilityContext.Provider
      value={{ facilities, currentFacility, currentFacilityId, setCurrentFacilityId, loading }}
    >
      {children}
    </FacilityContext.Provider>
  )
}

export function useFacility() {
  return useContext(FacilityContext)
}