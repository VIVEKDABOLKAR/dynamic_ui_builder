import React, { useEffect, useState } from 'react'
import apiClient from '../../../api/apiClient'
import { getNavbarStyle, getNavbarComponents } from '../../../api/globalUiApi'
import Profile from '../../../components/admin/Profile'
import FacilityListComponent from './FacilityListComponent'
import { useFacility } from '../../../context/FacilityV2Context'

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

function ProfileComponent() {
  return (
    <span className="flex h-7 w-7 items-center justify-center rounded-full bg-white/20 text-xs font-semibold">
      AD
    </span>
  )
}

export default function   DynamicNavbar() {
  const {
    facilities,
    selectedFacility,
    changeFacility,
  } = useFacility();

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


  // Reload whenever facility changes
  useEffect(() => {
    if (!selectedFacility?.id) return;

    let mounted = true;

    const loadComponents = async () => {
      try {
        const data = await getNavbarComponents(selectedFacility?.id);

        if (mounted) {
          setComponents(data);
        }
      } catch (err) {
        console.error("Failed to load navbar components", err);

        if (mounted) {
          setComponents(null);
        }
      }
    };

    loadComponents();

    return () => {
      mounted = false;
    };
  }, [selectedFacility?.id]);

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
        {showLogo && <LogoComponent imageUrl={components?.logoUrl} facilityName={facilities.name} />}
      </div>
      <div className="flex items-center gap-3">
        {showFacilities && (
          <FacilityListComponent />
        )}
        {showProfile && <Profile />}
      </div>
    </nav>
  )
}