import React, { useEffect, useState } from 'react'
import { AgGridReact } from 'ag-grid-react'
import { Link } from 'react-router-dom'
import { getAllPages } from '../../../../api/adminPageApi'

export default function ManagePage() {
    const [rowData, setRowData] = useState([])

    const columnDefs = [
        { field: 'id', minWidth: 90 },
        { field: 'pageName', minWidth: 180 },
        { field: 'pageCode', minWidth: 180 },
        { field: 'description', minWidth: 260, flex: 1 },
        {
            field: 'isActive',
            headerName: 'Status',
            minWidth: 120,
            valueFormatter: (params) => (params.value ? 'Active' : 'Inactive'),
        },
        {
            field: 'createdAt',
            minWidth: 180,
        },
        {
            headerName: 'Action',
            minWidth: 140,
            sortable: false,
            filter: false,
            cellRenderer: (params) => (
                <Link
                    to={`/admin_panel/manage_page/${params.data.pageCode}/edit`}
                    className="rounded-full bg-slate-900 px-4 py-2 text-xs font-semibold text-white transition hover:bg-slate-700"
                >
                    Edit
                </Link>
            ),
        },
        {
            headerName: 'Add Component',
            minWidth: 180,
            sortable: false,
            filter: false,
            cellRenderer: (params) => (
                <Link
                    to={`/admin_panel/manage_page/${params.data.pageCode}/components`}
                    className="rounded-full bg-cyan-500 px-4 py-2 text-xs font-semibold text-white transition hover:bg-cyan-400"
                >
                    Add Component
                </Link>
            ),
        },
    ]

    useEffect(() => {
        const fetchPages = async () => {
            try {
                const res = await getAllPages()
                setRowData(Array.isArray(res) ? res : [])
            } catch (error) {
                console.error('Failed to load pages', error)
            }
        }

        fetchPages()
    }, [])

    return (
        <div className="space-y-4">
            <div className="flex flex-col gap-3 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:flex-row sm:items-center sm:justify-between">
                <div>
                    <h1 className="text-xl font-semibold text-slate-900">Manage Pages</h1>
                    <p className="text-sm text-slate-500">Create and review UI pages.</p>
                </div>

                <Link
                    to="add"
                    className="inline-flex w-fit items-center rounded-full bg-cyan-400 px-4 py-2 text-sm font-semibold text-slate-950 transition hover:bg-cyan-300"
                >
                    + Add New Page
                </Link>
            </div>

            <div className="rounded-2xl border border-slate-200 bg-white shadow-sm">
                <div className="flex items-center justify-between border-b border-slate-100 px-4 py-3 sm:px-5">
                    <div>
                        <h2 className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
                            List of Pages
                        </h2>
                    </div>
                </div>

                <div className="overflow-auto">
                    <div className="h-[350px] ">
                        <AgGridReact rowData={rowData} columnDefs={columnDefs} />
                    </div>
                </div>
            </div>
        </div>
    )
}
