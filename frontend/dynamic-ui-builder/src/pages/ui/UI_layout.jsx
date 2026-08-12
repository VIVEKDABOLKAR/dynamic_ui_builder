import React, { useEffect, useState } from 'react'
import { Outlet } from 'react-router-dom'
import DynamicNavbar from '../admin/globalUi/DynamicNavbar';
import { getAccessibleFacilities } from '../../api/facilityApi'
import DynamicSideBar from '../../components/dynamicPageRender/layout/DynamicSideBar';

export default function UI_layout() {


  return (
    <>
      {/* // h-screen + flex-col: navbar takes its natural height, the row below
    // takes everything that's left, so the sidebar can be h-full inside it. */}
      <div className="flex h-dvh flex-col overflow-hidden">
        <DynamicNavbar />

        {/* flex-1 + overflow-hidden = a bounded row; sidebar and main content
          scroll independently instead of pushing the whole page down. */}
        <div className="flex flex-1 min-h-0 overflow-hidden">
          <DynamicSideBar />

          <main className="m-4 flex-1 min-h-0 overflow-y-auto rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
            <Outlet />
          </main>
        </div>
      </div>
    </>
  )
}