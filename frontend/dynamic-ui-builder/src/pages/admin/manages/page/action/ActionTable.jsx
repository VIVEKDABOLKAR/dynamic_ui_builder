import { AgGridReact } from 'ag-grid-react';
import React, { useState } from 'react'

export default function ActionTable() {
    const [isLoading, setIsLoading] = useState(false)
    const[actions, SetActions] = useState([]);

    const actionColumnDefs = [
        {
            field: "id",
            hide: true,
        },
        {
            field: "actionName",
            headerName: "Action Name",
            minWidth: 100,
            flex: 1,
        },
        {
            field: "actionType",
            headerName: "Action Type",
            minWidth: 160,
        },
        {
            field: "properties",
            headerName: "Configuration",
            flex: 2,
            valueFormatter: (params) => {
                try {
                    return JSON.stringify(
                        typeof params.value === "string"
                            ? JSON.parse(params.value)
                            : params.value
                    );
                } catch {
                    return params.value;
                }
            },
        },
        {
            headerName: "Action",
            field: "actions",
            pinned: "right",
            lockPinned: true,
            sortable: false,
            filter: false,
            minWidth: 180,
            maxWidth: 220,
            cellRenderer: (params) => (
                <div className="flex h-full items-center gap-2 py-1">
                    <button
                        type="button"
                        onClick={() => handleEditAction(params.data)}
                        className="rounded-md bg-amber-100 px-2 py-1 text-xs font-semibold text-amber-700 hover:bg-amber-200"
                    >
                        Edit
                    </button>

                    <button
                        type="button"
                        onClick={() => handleDeleteAction(params.data.id)}
                        className="rounded-md bg-red-100 px-2 py-1 text-xs font-semibold text-red-700 hover:bg-red-200"
                    >
                        Delete
                    </button>
                </div>
            ),
        },
    ];
    return (
        <div className="h-[360px] w-full">
            {isLoading ? (
                <div className="flex h-full items-center justify-center gap-3 text-sm text-slate-500">
                    <span className="h-5 w-5 animate-spin rounded-full border-2 border-slate-300 border-t-cyan-500" />
                    Loading actions...
                </div>
            ) : (
                <AgGridReact
                    rowData={actions}
                    columnDefs={actionColumnDefs}
                    defaultColDef={{
                        sortable: true,
                        filter: true,
                        resizable: true,
                    }}
                />
            )}
        </div>
    )
}
