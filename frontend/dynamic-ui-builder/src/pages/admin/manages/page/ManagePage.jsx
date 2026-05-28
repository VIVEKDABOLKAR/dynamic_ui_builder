import React, { useEffect, useState } from 'react'
import { AgGridReact } from 'ag-grid-react'
import { Link } from 'react-router-dom'
import { deletePage, getAllPages } from '../../../../api/adminPageApi'

export default function ManagePage() {
    const [rowData, setRowData] = useState([])
    const [isLoading, setIsLoading] = useState(false)

    const loadPages = async () => {
        setIsLoading(true)
        try {
            const res = await getAllPages()
            setRowData(Array.isArray(res) ? res : [])
        } catch (error) {
            console.error('Failed to load pages', error)
            setRowData([])
        } finally {
            setIsLoading(false)
        }
    }

    const handleDelete = async (pageCode) => {
        const confirmed = window.confirm('Delete this page?')
        if (!confirmed) return

        try {
            await deletePage(pageCode)
            await loadPages()
        } catch (error) {
            console.error('Failed to delete page', error)
        }
    }

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
            minWidth: 320,
            maxWidth: 380,
            pinned: 'right',
            lockPinned: true,
            lockPosition: true,
            suppressMovable: true,
            sortable: false,
            filter: false,
            cellRenderer: (params) => (
                <div className="flex h-full items-center gap-2 py-1">
                    <Link
                        to={`/admin_panel/manage_page/${params.data.pageCode}/edit`}
                        className="rounded-full bg-slate-900 px-3 py-1 text-xs font-semibold text-white transition hover:bg-slate-700"
                    >
                        Edit
                    </Link>
                    <button
                        type="button"
                        onClick={() => handleDelete(params.data.pageCode)}
                        className="rounded-full bg-red-500 px-3 py-1 text-xs font-semibold text-white transition hover:bg-red-400"
                    >
                        Delete
                    </button>
                    <Link
                        to={`/admin_panel/manage_page/${params.data.pageCode}/components`}
                        className="rounded-full bg-cyan-500 px-3 py-1 text-xs font-semibold text-white transition hover:bg-cyan-400"
                    >
                        Add Component
                    </Link>
                </div>
            ),
        },
    ]

    useEffect(() => {
        loadPages()
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
                        {isLoading ? (
                            <div className="flex h-full items-center justify-center gap-3 text-sm text-slate-500">
                                <span className="h-5 w-5 animate-spin rounded-full border-2 border-slate-300 border-t-cyan-500" />
                                Loading pages...
                            </div>
                        ) : rowData.length === 0 ? (
                            <div className="flex h-full flex-col items-center justify-center gap-2 text-sm text-slate-500">
                                <p className="font-semibold text-slate-700">No pages yet</p>
                                <p>Create a new page to get started.</p>
                            </div>
                        ) : (
                            <AgGridReact rowData={rowData} columnDefs={columnDefs} />
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}
