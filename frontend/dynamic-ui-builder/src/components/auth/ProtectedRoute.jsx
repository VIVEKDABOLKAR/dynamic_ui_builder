import React from 'react'
import { Outlet, Navigate } from 'react-router-dom'
import { isLoggedIn, getRole } from '../../api/authApi'

export default function ProtectedRoute({ children, requiredRole }) {
  const loggedIn = isLoggedIn()
  const role = getRole()

  if (!loggedIn) {  
    return <Navigate to="/login" replace />
  }

  if (requiredRole && role !== requiredRole) {
    return <Navigate to="/login" replace />
  }

  // Support both usage patterns:
  // <ProtectedRoute><AdminLayout /></ProtectedRoute>  → renders children
  // <ProtectedRoute /> as a layout route              → renders Outlet
  return children ?? <Outlet />
}