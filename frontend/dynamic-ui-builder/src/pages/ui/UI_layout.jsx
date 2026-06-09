import React from 'react'
import Navbar from '../../components/admin/Navbar'
import { Outlet } from 'react-router'

export default function UI_layout() {
  return (
    <>
    <Navbar />

    <hr />

    <Outlet />
    </>
  )
}
