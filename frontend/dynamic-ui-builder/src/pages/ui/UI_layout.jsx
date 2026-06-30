import React from 'react'
import Navbar from '../../components/admin/Navbar'
import { Outlet, useNavigate } from 'react-router'

export default function UI_layout() {
  const navigate = useNavigate();

  const handleSidebarChange = () => {
		navigate(-1)
	}

  return (
    <>
    <Navbar handleSidebarChange={handleSidebarChange}/>

    <hr />

    <Outlet />
    </>
  )
}
