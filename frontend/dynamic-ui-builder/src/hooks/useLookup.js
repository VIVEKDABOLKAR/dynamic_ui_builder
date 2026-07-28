import { useEffect, useState } from 'react'
import { getLookupsByType } from '../api/lookupApi'

/**
 * Fetches active UILookup rows for a given lookupType (e.g. "MODULE_CODE").
 * Returns { options, loading } where options is [{ value, label }].
 */
export default function useLookup(lookupType) {
  const [options, setOptions] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false

    const fetchLookups = async () => {
      try {
        setLoading(true)
        const data = await getLookupsByType(lookupType)
        if (!cancelled) {
          setOptions(
            (data ?? []).map((lookup) => ({
              value: lookup.lookupValue,
              label: lookup.displayValue || lookup.lookupValue,
            }))
          )
        }
      } catch (error) {
        console.error(`failed to load lookups for ${lookupType}`, error)
        if (!cancelled) setOptions([])
      } finally {
        if (!cancelled) setLoading(false)
      }
    }

    fetchLookups()
    return () => {
      cancelled = true
    }
  }, [lookupType])

  return { options, loading }
}
