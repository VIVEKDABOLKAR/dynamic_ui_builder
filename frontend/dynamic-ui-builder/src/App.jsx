import React from 'react'

import { Navigate, Route, Routes } from 'react-router-dom'
import Landing from './pages/landing/Landing'
import AdminLayout from './pages/admin/Admin_Layout'
import ManagePage from './pages/admin/manages/page/ManagePage'
import AddNewPage from './pages/admin/manages/page/AddNewPage'
import DynamicPageRendering from './pages/ui/DynamicPageRendering'

export default function App() {
  return (
    <Routes>
      <Route path='/' element={<Landing />} />
      <Route path='/admin_panel' element={<AdminLayout />}>
        <Route
          index
          element={
            <div className="rounded-xl border border-slate-200 bg-white p-4 text-sm text-slate-600 shadow-sm">
              Admin overview
            </div>
          }
        />
        <Route path='manage_page' element={<ManagePage />} />
        <Route path='manage_page/add' element={<AddNewPage />} />
      </Route>

      <Route path='/ui/*' element={<DynamicPageRendering />}>

      </Route>
      {/* <Route path='*' element={<Navigate to='/' replace />} /> */}
    </Routes>
  )
}
