import React from 'react'
import { useNavigate } from 'react-router-dom'
import { logout, getUsername } from '../../api/authApi'
import Profile from './Profile'
import FacilityListComponent from '../../pages/admin/globalUi/FacilityListComponent'

export default function Navbar({ sidebarOpen, handleSidebarChange }) {
  const navigate = useNavigate()
  const username = getUsername() || 'Admin'
  const initial = username.charAt(0).toUpperCase()

  const handleLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }
  console.log("sidbar open const ", sidebarOpen)
  return (
    <header className="flex items-center justify-between border-b border-slate-200 bg-blue-100 px-4 py-4 sm:px-6">

      <div className='flex flex-row gap-3 items-center'>
        {!sidebarOpen &&
          <button
            onClick={handleSidebarChange}
            className="rounded p-2 hover:bg-slate-200"
          >
            {sidebarOpen === undefined ? '❮' : '☰'}
          </button>
        }

        <div>
          <p className="text-sm font-medium text-slate-500">Welcome back</p>
          <h2 className="text-lg font-semibold text-slate-900">{username}</h2>
        </div>
      </div>

      <div className="flex items-center gap-3">
        <FacilityListComponent />

        <Profile />
      </div>

    </header>
  )
}