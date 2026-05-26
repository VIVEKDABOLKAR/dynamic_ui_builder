import React from 'react'
import { Outlet } from 'react-router-dom'
import Sidebar from '../../components/admin/Sidebar'
import Navbar from '../../components/admin/Navbar'

export default function AdminLayout() {
	return (
		<div className="min-h-screen bg-slate-50 text-slate-900 lg:grid lg:grid-cols-[240px_1fr]">
			<Sidebar />
			<div className="flex min-h-screen flex-col">
				<Navbar />
				<main className="flex-1 overflow-hidden p-4 sm:p-6 lg:p-8">
					<div className="h-full rounded-xl border border-slate-200 bg-white p-4 text-sm text-slate-600 shadow-sm">

						<Outlet />
					</div>
				</main>
			</div>
		</div>
	)
}
