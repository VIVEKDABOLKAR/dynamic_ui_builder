import React from 'react'
import { useNavigate } from 'react-router-dom'
import { logout, getUsername } from '../../api/authApi'

export default function Navbar({ sidebarOpen, handleSidebarChange }) {
  const navigate = useNavigate()
  const username = getUsername() || 'Admin'
  const initial = username.charAt(0).toUpperCase()

  const handleLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <header className="flex items-center justify-between border-b border-slate-200 bg-blue-100 px-4 py-4 sm:px-6">


      <div className='flex flex-row gap-3 items-center'>
        {!sidebarOpen && 
          <button
            onClick={handleSidebarChange}
            className="rounded p-2 hover:bg-slate-200"
          >
            ☰
          </button>
        }

        <div>
          <p className="text-sm font-medium text-slate-500">Welcome back</p>
          <h2 className="text-lg font-semibold text-slate-900">{username}</h2>
        </div>
      </div>

      <div className="flex items-center gap-3">

        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-slate-900 text-sm font-semibold text-white">
          {initial}
        </div>
        <button
          onClick={handleLogout}
          className="rounded-xl border border-slate-200 bg-slate-50 px-4 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100"
        >
          Logout
        </button>
      </div>

    </header>
  )
}