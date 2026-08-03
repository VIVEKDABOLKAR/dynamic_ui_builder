import React, { useEffect, useState } from 'react'
import { getAccessibleFacilities } from '../../../api/facilityApi'
import { useFacility } from '../../../context/FacilityV2Context';

export default function FacilityListComponent() {
  const {facilities, selectedFacility, changeFacility, loading} = useFacility();


  //handle facility selection
  const handleChange = (e) => {
    const facilityId = e.target.value

        changeFacility(facilityId); 
  }

  if(loading) {
    return (
    <div className="h-9 w-40 animate-pulse rounded-md bg-white/20" />
  );
  }

  return (
    <select
      value={selectedFacility?.id}
      onChange={handleChange}
      className="rounded-md bg-white/15 px-2 py-1 text-sm font-medium outline-none"
    >
      {facilities.map((f) => (
        <option
          key={f.id}
          value={f.id}
          className="text-slate-900">
          {f.name}
        </option>
      ))}
    </select>
  )
}
