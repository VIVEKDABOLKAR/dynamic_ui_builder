import React from 'react'
import { Navigate } from 'react-router-dom'
import { isLoggedIn, getRole } from '../../api/authApi'

export default function ProtectedRoute({ children, requiredRole }) {
  if (!isLoggedIn()) {
    return <Navigate to="/login" replace />
  }

  if (requiredRole && getRole() !== requiredRole) {
    return <Navigate to="/login" replace />
  }

  return children
}