import { AgGridReact } from 'ag-grid-react';
import React, { useEffect, useState } from 'react'
import { deletePageAction, getActionsByPageCode } from '../../../../../api/actionsPageApi';

export default function ActionTable({ pageCode, onEdit, refreshKey }) {
    const [isLoading, setIsLoading] = useState(false);
    const [actions, setActions] = useState([]);

    const loadActions = async () => {
        if (!pageCode) return;
        setIsLoading(true);
        try {
            const data = await getActionsByPageCode(pageCode);
            setActions(Array.isArray(data) ? data : []);
        } catch (error) {
            console.error('Failed to load actions', error);
            setActions([]);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        loadActions();
    }, [pageCode, refreshKey]);

    const handleDeleteAction = async (id) => {
        const confirmed = window.confirm('Delete this action?');
        if (!confirmed) return;
        try {
            await deletePageAction(id);
            await loadActions();
        } catch (error) {
            console.error('Failed to delete action', error);
        }
    };

    const actionColumnDefs = [
        {
            field: "id",
            hide: true,
        },
        {
            field: "actionName",
            headerName: "Action Name",
            minWidth: 140,
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
                        onClick={() => onEdit && onEdit(params.data)}
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
            ) : actions.length === 0 ? (
                <div className="flex h-full flex-col items-center justify-center gap-2 text-sm text-slate-500">
                    <p className="font-semibold text-slate-700">No actions yet</p>
                    <p>Add your first action using the form above.</p>
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
