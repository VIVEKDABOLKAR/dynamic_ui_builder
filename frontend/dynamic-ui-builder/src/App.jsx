import React from 'react'
import { Route, Routes } from 'react-router-dom'
import Landing from './pages/landing/Landing'
import Login from './pages/auth/Login'
import AdminLayout from './pages/admin/Admin_Layout'
import ManagePage from './pages/admin/manages/page/ManagePage'
import AddNewPage from './pages/admin/manages/page/AddNewPage'
import ManagePageComponents from './pages/admin/manages/page/ManagePageComponents'
import DynamicPageRendering from './pages/ui/DynamicPageRendering'
import DynamicPage from './pages/ui/DynamicPage'
import PageJson from './pages/admin/pageJson/PageJson'
import ManagePageAction from './pages/admin/manages/page/action/ManagePageAction'
import AdminOverview from './pages/admin/AdminOverview'
import DynamicPageRenderingPageForm from './pages/ui/DynamicPageRenderingPageForm'
import ProtectedRoute from './components/auth/ProtectedRoute'
import UI_layout from './pages/ui/UI_layout'
import Signup from './pages/auth/Signup'
import ViewerHome from './pages/ui/ViewerHome'
export default function App() {
  return (
    <Routes>

      {/* Public */}
      <Route path="/" element={<Landing />} />

      <Route path="/login" element={<Login />} />
      <Route path="/signup" element={<Signup />} />

      {/* Admin — ROLE_ADMIN only */}
      <Route element={<ProtectedRoute requiredRole="ROLE_ADMIN" />}>
        <Route path="/admin_panel" element={<AdminLayout />}>
          <Route path="overview" element={<AdminOverview />} />
          <Route path="manage_page" element={<ManagePage />} />
          <Route path="manage_page/add" element={<AddNewPage />} />
          <Route path="manage_page/:pageCode/edit" element={<AddNewPage />} />
          <Route path="manage_page/:pageCode/components" element={<ManagePageComponents />} />
          <Route path="manage_page/:pageCode/action" element={<ManagePageAction />} />
          <Route path="page_json" element={<PageJson />} />
        </Route>
      </Route>

      {/* UI pages — any authenticated user */}
      <Route element={<ProtectedRoute requiredRole="ROLE_ADMIN" />}>
        <Route element={<UI_layout />}>
        <Route path="/ui" element={<ViewerHome />} />   
          <Route path="/ui/*" element={<DynamicPageRendering />} />
        </Route>
      </Route>

      <Route element={<UI_layout />}>
        <Route path="/ui_demo/*" element={<DynamicPage />} />
      </Route>

    </Routes>
  )
}