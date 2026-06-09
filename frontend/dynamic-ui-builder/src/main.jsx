import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'

//ag-grid
import { AgGridProvider } from 'ag-grid-react'
import { AllCommunityModule } from 'ag-grid-community'

import './index.css'
import App from './App.jsx'
import Provider from './Provider.jsx'

const modules = [AllCommunityModule];

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Provider>
        <App />
    </Provider>
  </StrictMode>,
)
