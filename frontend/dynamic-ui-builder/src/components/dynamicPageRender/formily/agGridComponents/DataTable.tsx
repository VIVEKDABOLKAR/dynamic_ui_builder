import React, { useEffect, useState } from "react";

import { AgGridReact } from "ag-grid-react";
import { MappingSchema } from "../../types/JsonSchema";
import { resolveMapping } from "../../../dataMappingEngine/mappingResolver";
import { DynamicRows, generateColumns } from "../../../dataMappingEngine/utils/generateColumns";

// import "ag-grid-community/styles/ag-grid.css";
// import "ag-grid-community/styles/ag-theme-alpine.css";

interface DataTableProps {

    label?: string;

    placeholder?: string;

    columns?: any[];

    data?: any[];

    height?: number;

    mapping?: MappingSchema
}

export const DataTable = ({
    label,
    placeholder,
    columns = [],
    data = [],
    height = 400,
    mapping,
}: DataTableProps) => {
    const [rows, setRows] = useState([]);
    const [dynamicColumns, setDynamicColumns] = useState([]);

    useEffect(() => {
        const loadData = async () => {
            const data = await resolveMapping(mapping);
            const generated = generateColumns(data);

            setDynamicColumns(generated as any);
            setRows(data);

        }

        loadData();
    }, []);

    console.log("rendering DataTable with rows:", rows, "and columns:", dynamicColumns);

    return (

        <div
            style={{
                width: "100%",
            }}
            className="mt-5 overflow-hidden rounded-3xl border border-slate-200 bg-gradient-to-br from-white via-slate-50 to-slate-100 shadow-[0_18px_50px_-24px_rgba(15,23,42,0.35)]"
        >
            <div className="flex items-start justify-between gap-4 border-b border-slate-200/80 bg-white/70 px-5 py-4 backdrop-blur">
                <div className="space-y-1">
                    {label && (
                        <h2 className="text-lg font-semibold tracking-tight text-slate-900">
                            {label}
                        </h2>
                    )}
                    {placeholder && (
                        <p className="max-w-2xl text-sm text-slate-500">
                            {placeholder}
                        </p>
                    )}
                </div>
                <div className="hidden rounded-full border border-cyan-200 bg-cyan-50 px-3 py-1 text-xs font-medium text-cyan-700 sm:block">
                    Dynamic table
                </div>
            </div>

            <div className="p-4 sm:p-5">
                <div
                    className="ag-theme-alpine overflow-hidden rounded-2xl border border-slate-200 shadow-sm"
                    style={{
                        width: "100%",
                        height
                    }}
                >
                    <AgGridReact
                        rowData={rows}
                        columnDefs={dynamicColumns}
                        pagination={true}
                        paginationPageSize={10}
                        paginationPageSizeSelector={[10, 20, 50, 100]}
                    />
                </div>
            </div>
        </div>
    );
};