import React, { useEffect, useState } from "react";

import { AgGridReact } from "ag-grid-react";
import { MappingSchema } from "../../types/JsonSchema";
import { resolveMapping } from "../../../dataMappingEngine/mappingResolver";
import { DynamicRows, generateColumns } from "../../../dataMappingEngine/utils/generateColumns";

// import "ag-grid-community/styles/ag-grid.css";
// import "ag-grid-community/styles/ag-theme-alpine.css";

interface DataTableProps {

    title?: string;

    columns?: any[];

    data?: any[];

    height?: number;

    mapping?: MappingSchema
}

export const DataTable = ({
    title,
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
                marginTop: 20
            }}
        >

            {title && (
                <h2>{title}</h2>
            )}

            <div
                className="ag-theme-alpine"
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
    );
};