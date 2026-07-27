import React, { useEffect, useState } from 'react'
import apiClient from '../../../api/apiClient'
import { getNavbarStyle, getNavbarComponents } from '../../../api/globalUiApi'

const DEFAULT_STYLE = {
  backgroundColor: '#1E3A8A',
  textColor: '#FFFFFF',
  height: '64px',
}

function LogoComponent({ imageUrl, facilityName }) {
  return imageUrl
    ? <img src={imageUrl} alt={facilityName} className="h-8 w-8 rounded-md object-contain" />
    : <span className="flex h-8 w-8 items-center justify-center rounded-md bg-white/15 text-xs font-bold">YMS</span>
}

function FacilityListComponent({ selectedFacilityId, onSelect, accessibleFacilities }) {
  const facilities = accessibleFacilities || []
  return (
    <select
      value={selectedFacilityId || ''}
      onChange={(e) => onSelect(e.target.value)}
      className="rounded-md bg-white/15 px-2 py-1 text-sm font-medium outline-none"
    >
      {facilities.map((f) => (
        <option key={f.id} value={f.id} className="text-slate-900">{f.name}</option>
      ))}
    </select>
  )
}

function ProfileComponent() {
  return (
    <span className="flex h-7 w-7 items-center justify-center rounded-full bg-white/20 text-xs font-semibold">
      AD
    </span>
  )
}

export default function DynamicNavbar({ facilityId, facilityName, accessibleFacilities, onFacilityChange }) {
  const [style, setStyle] = useState(null)
  const [components, setComponents] = useState(null)

  // Style: fetched once, never changes with facility
  useEffect(() => {
    let mounted = true
    getNavbarStyle()
      .then((data) => mounted && setStyle(data))
      .catch(() => mounted && setStyle(null))
    return () => { mounted = false }
  }, [])

  // Components: re-fetched every time facilityId changes
  useEffect(() => {
    if (!facilityId) return
    let mounted = true
    getNavbarComponents(facilityId)
      .then((data) => mounted && setComponents(data))
      .catch(() => mounted && setComponents(null))
    return () => { mounted = false }
  }, [facilityId])

  const finalStyle = style || DEFAULT_STYLE
  const showLogo = components?.showLogo ?? true
  const showProfile = components?.showProfile ?? true
  const showFacilities = components?.showFacilities ?? true

  return (
    <nav
      className="flex items-center justify-between px-4"
      style={{
        backgroundColor: finalStyle.backgroundColor,
        color: finalStyle.textColor,
        height: finalStyle.height,
      }}
    >
      <div className="flex items-center gap-2">
        {showLogo && <LogoComponent imageUrl={components?.logoUrl} facilityName={facilityName} />}
      </div>
      <div className="flex items-center gap-3">
        {showFacilities && (
          <FacilityListComponent
            selectedFacilityId={facilityId}
            onSelect={onFacilityChange}
            accessibleFacilities={accessibleFacilities}
          />
        )}
        {showProfile && <ProfileComponent />}
      </div>
    </nav>
  )
}