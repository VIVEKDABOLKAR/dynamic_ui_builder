import React from 'react'

import { AgGridProvider } from 'ag-grid-react'
import { ModuleRegistry, AllCommunityModule } from 'ag-grid-community'

import { BrowserRouter, Outlet } from 'react-router-dom'

import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";


ModuleRegistry.registerModules([AllCommunityModule]);

export default function Provider({ children }) {
    return (
        <LocalizationProvider dateAdapter={AdapterDayjs}>
            <BrowserRouter>
                {children}
            </ BrowserRouter >
        </LocalizationProvider>

    )
}
