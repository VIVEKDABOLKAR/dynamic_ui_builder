import React, { useEffect, useState } from "react";
import { createLookup, createLookupMaster, deleteLookup, deleteLookupMaster, getLookupsMasters, getLookupValuesByMaster, updateLookup, updateLookupMaster } from "../../../../api/lookupApi";
import { AgGridReact } from "ag-grid-react";
import LookupMasterDialog from "../../../../components/admin/lookup/LookupMasterDialog";
import { FaEdit, FaTrash } from "react-icons/fa";
import LookupDialog from "../../../../components/admin/lookup/LookupDialog";



export default function LookupManagement() {
    const [lookupMasters, setLookupMasters] = useState([]);
    const [selectedMaster, setSelectedMaster] = useState(null);

    const [lookupValues, setLookupValues] = useState([]);

    const [masterDialogOpen, setMasterDialogOpen] = useState(false);
    const [lookupDialogOpen, setLookupDialogOpen] = useState(false);

    const [editingMaster, setEditingMaster] = useState(null);
    const [editingLookup, setEditingLookup] = useState(null);

    const [loading, setLoading] = useState(false);

    useEffect(() => {
        loadLookupMasters();
    }, []);

    const loadLookupMasters = async () => {
        try {
            setLoading(true);

            const data = await getLookupsMasters();

            setLookupMasters(data);

            if (data.length > 0) {
                handleSelectMaster(data[0]);
            }
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleSelectMaster = async (master) => {
        try {
            setSelectedMaster(master);

            const data = await getLookupValuesByMaster(master.id);

            setLookupValues(data);
        } catch (err) {
            console.error(err);
        }
    };

    //---------------------------
    // Lookup Master CRUD
    //---------------------------

    const handleAddMaster = () => {
        setEditingMaster(null);
        setMasterDialogOpen(true);
    };

    const handleEditMaster = (master) => {
        setEditingMaster(master);
        setMasterDialogOpen(true);
    };

    const handleSaveMaster = async (payload) => {
        try {
            if (editingMaster) {
                await updateLookupMaster(editingMaster.id, payload);
            } else {
                await createLookupMaster(payload);
            }

            setMasterDialogOpen(false);

            loadLookupMasters();
        } catch (err) {
            console.error(err);
        }
    };

    const handleDeleteMaster = async (id) => {
        if (!window.confirm("Delete Lookup Master?")) return;

        try {
            await deleteLookupMaster(id);

            setSelectedMaster(null);
            setLookupValues([]);

            loadLookupMasters();
        } catch (err) {
            console.error(err);
        }
    };

    //---------------------------
    // Lookup CRUD
    //---------------------------

    const handleAddLookup = () => {
        setEditingLookup(null);
        setLookupDialogOpen(true);
    };

    const handleEditLookup = (lookup) => {
        setEditingLookup(lookup);
        setLookupDialogOpen(true);
    };

    const handleSaveLookup = async (payload) => {
        payload.lookupMasterId = selectedMaster.id;
        payload.lookupType = selectedMaster.lookupName;

        if (editingLookup) {
            await updateLookup(editingLookup.id, payload);
        } else {
            await createLookup(payload);
        }

        setLookupDialogOpen(false);
        handleSelectMaster(selectedMaster);
    };

    const handleDeleteLookup = async (id) => {
        if (!window.confirm("Delete Lookup?")) return;

        try {
            await deleteLookup(id);

            handleSelectMaster(selectedMaster);
        } catch (err) {
            console.error(err);
        }
    };

    //for Ag grid defs
    const masterColumnDefs = [
        {
            field: "id",
            minWidth: 80,
            filter: true,
        },
        {
            field: "lookupName",
            headerName: "Lookup Name",
            minWidth: 180,
            filter: true,
        },
        {
            field: "description",
            flex: 1,
            minWidth: 250,
            filter: true,
        },
        {
            field: "componentId",
            headerName: "Component",
            minWidth: 130,
            filter: true,
        },
        {
            field: "isActive",
            headerName: "Status",
            minWidth: 120,
            valueFormatter: (params) =>
                params.value ? "Active" : "Inactive",
            cellStyle: (params) => ({
                cursor: "pointer",
                color: params.value ? "#16a34a" : "#dc2626",
                fontWeight: 600,
            }),
        },
        {
            headerName: "Action",
            minWidth: 160,
            pinned: "right",
            sortable: false,
            filter: false,
            cellRenderer: (params) => (
                <div className="flex h-full items-center gap-2">

                    <button
                        onClick={() => handleEditMaster(params.data)}
                        className="rounded-full bg-slate-900 p-2 text-white hover:bg-slate-700"
                    >
                        <FaEdit size={14} />
                    </button>

                    <button
                        onClick={() => handleDeleteMaster(params.data.id)}
                        className="rounded-full bg-red-500 p-2 text-white hover:bg-red-400"
                    >
                        <FaTrash size={14} />
                    </button>

                </div>
            ),
        },
    ];

    const lookupColumnDefs = [
        {
            field: "displayValue",
            headerName: "Display Value",
            minWidth: 180,
            filter: true,
        },
        {
            field: "lookupValue",
            headerName: "Lookup Value",
            minWidth: 150,
            filter: true,
        },
        {
            field: "lookupType",
            headerName: "Lookup Type",
            minWidth: 180,
            filter: true,
        },
        {
            field: "sequenceNo",
            headerName: "Sequence",
            minWidth: 120,
            filter: true,
        },
        {
            field: "isActive",
            headerName: "Status",
            minWidth: 120,
            valueFormatter: (params) =>
                params.value ? "Active" : "Inactive",
            cellStyle: (params) => ({
                color: params.value ? "#16a34a" : "#dc2626",
                fontWeight: 600,
            }),
        },
        {
            headerName: "Action",
            minWidth: 160,
            pinned: "right",
            sortable: false,
            filter: false,
            cellRenderer: (params) => (
                <div className="flex h-full items-center gap-2">

                    <button
                        onClick={() => handleEditLookup(params.data)}
                        className="rounded-full bg-slate-900 p-2 text-white hover:bg-slate-700"
                    >
                        <FaEdit size={14} />
                    </button>

                    <button
                        onClick={() => handleDeleteLookup(params.data.id)}
                        className="rounded-full bg-red-500 p-2 text-white hover:bg-red-400"
                    >
                        <FaTrash size={14} />
                    </button>

                </div>
            ),
        },
    ];

    return (
        <div>
            <h2>Lookup Management</h2>

            {/* Lookup Master Table */}
            <div className="rounded-2xl border border-slate-200 bg-white shadow-sm">

                <div className="flex items-center justify-between border-b border-slate-100 px-5 py-3">

                    <div>
                        <h2 className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
                            Lookup Masters
                        </h2>
                    </div>

                    <button
                        onClick={handleAddMaster}
                        className="rounded-full bg-cyan-400 px-4 py-2 text-sm font-semibold hover:bg-cyan-300"
                    >
                        + Add Lookup Master
                    </button>

                </div>

                <div className="h-[320px]">

                    <AgGridReact
                        rowData={lookupMasters}
                        columnDefs={masterColumnDefs}
                        onRowClicked={(event) => handleSelectMaster(event.data)}
                    />

                </div>

            </div>

            {/* Add/Edit Master Dialog */}
            <LookupMasterDialog open={masterDialogOpen}
                onClose={() => { setMasterDialogOpen(false) }}
                onSave={handleSaveMaster}
                editingMaster={editingMaster} />


            {/* Lookup Values Table */}
            <div className="rounded-2xl border border-slate-200 bg-white shadow-sm mt-6">

                <div className="flex items-center justify-between border-b border-slate-100 px-5 py-3">

                    <div>

                        <h2 className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
                            Lookup Values
                        </h2>

                        {selectedMaster && (
                            <p className="text-xs text-slate-500 mt-1">
                                Selected : {selectedMaster.lookupName}
                            </p>
                        )}

                    </div>

                    <button
                        onClick={handleAddLookup}
                        disabled={!selectedMaster}
                        className="rounded-full bg-cyan-400 px-4 py-2 text-sm font-semibold hover:bg-cyan-300 disabled:opacity-50"
                    >
                        + Add Lookup Value
                    </button>

                </div>

                <div className="h-[350px]">

                    <AgGridReact
                        rowData={lookupValues}
                        columnDefs={lookupColumnDefs}
                    />

                </div>

            </div>

            {/* Add/Edit Lookup Dialog */}
            <LookupDialog open={lookupDialogOpen}
                onClose={() => { setLookupDialogOpen(false) }}
                onSave={handleSaveLookup}
                editingLookup={editingLookup}
                selectedMaster={selectedMaster} />
        </div>
    );
}