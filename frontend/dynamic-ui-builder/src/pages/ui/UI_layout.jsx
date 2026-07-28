import React, { useEffect, useState } from 'react'
import { Outlet } from 'react-router-dom'
import DynamicNavbar from '../admin/globalUi/DynamicNavbar';
import { getAccessibleFacilities } from '../../api/facilityApi'
import DynamicSideBar from '../../components/dynamicPageRender/layout/DynamicSideBar';

export default function UI_layout() {
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

  if (loading) {
    return <div className="p-6 text-sm text-slate-500">Loading facilities…</div>
  }

  if (!facilities.length) {
    return <div className="p-6 text-sm text-slate-500">No facility access. Please contact an administrator.</div>
  }

  const currentFacility = facilities.find((f) => f.id === currentFacilityId) || null



  return (
    <>
    {/* // h-screen + flex-col: navbar takes its natural height, the row below
    // takes everything that's left, so the sidebar can be h-full inside it. */}
    <div className="flex h-screen flex-col overflow-hidden">
      <DynamicNavbar
        facilityId={currentFacilityId}
        facilityName={currentFacility?.name}
        accessibleFacilities={facilities}
        onFacilityChange={setCurrentFacilityId}
      />
        {/* flex-1 + overflow-hidden = a bounded row; sidebar and main content
          scroll independently instead of pushing the whole page down. */}
      <div className="flex flex-1 min-h-0">
        <DynamicSideBar />
      
      <main className="flex-1 overflow-y-auto bg-white">
        {/* <Outlet context={{ currentFacility, currentFacilityId }} /> */}
        
          <Outlet />
        </main>
      </div>
    </div>
    </>
  )
}