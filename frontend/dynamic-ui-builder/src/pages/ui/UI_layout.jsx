import React from 'react'
import Navbar from '../../components/admin/Navbar'
import { Outlet, useNavigate } from 'react-router'
import DynamicSideBar from '../../components/dynamicPageRender/layout/DynamicSideBar';

export default function UI_layout() {
  const navigate = useNavigate();

  const handleSidebarChange = () => {
    navigate(-1)
  }

  return (
    // h-screen + flex-col: navbar takes its natural height, the row below
    // takes everything that's left, so the sidebar can be h-full inside it.
    <div className="flex h-screen flex-col overflow-hidden">
      <Navbar handleSidebarChange={handleSidebarChange} />

      {/* flex-1 + overflow-hidden = a bounded row; sidebar and main content
          scroll independently instead of pushing the whole page down. */}
      <div className="flex flex-1 min-h-0">
        <DynamicSideBar />

        <main className="flex-1 overflow-y-auto bg-white">
          <Outlet />
        </main>
      </div>
    </div>
  )
}