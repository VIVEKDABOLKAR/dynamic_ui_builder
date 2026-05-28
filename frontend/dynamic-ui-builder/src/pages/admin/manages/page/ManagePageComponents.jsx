import React, { useEffect, useState } from 'react'
import { AgGridReact } from 'ag-grid-react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { createComponent, getComponentsByPage } from '../../../../api/componentApi'

const AVAILABLE_COMPONENTS = [
  { type: 'input', label: 'Text Field', icon: '🔤' },
  { type: 'textarea', label: 'Text Area', icon: '📝' },
  { type: 'button', label: 'Button', icon: '🔘' },
  { type: 'select', label: 'Dropdown', icon: '🔽' },
  { type: 'radio', label: 'Radio Button', icon: '⭕' },
  { type: 'checkbox', label: 'Checkbox', icon: '☑️' },
  { type: 'card', label: 'Card', icon: '🗂️' }
]

export default function ManagePageComponents() {
  const { pageCode } = useParams()
  const navigate = useNavigate()
  const [components, setComponents] = useState([])
  const [saving, setSaving] = useState(false)
  const [selectedComponent, setSelectedComponent] = useState(null)
  const [errorMessage, setErrorMessage] = useState('')
  const [formData, setFormData] = useState({
    componentName: '',
    componentType: AVAILABLE_COMPONENTS[0].type,
    labelName: '',
    placeholder: '',
    width: '',
    sequenceNo: 1,
    isRequired: false,
    isVisible: true,
    isDisabled: false,
  })
  const [lookupValues, setLookupValues] = useState([])
  const [newLookupValue, setNewLookupValue] = useState('')

  const columnDefs = [
    { field: 'id', hide: true },
    
    { field: 'componentName', headerName: 'Name', minWidth: 180, flex: 1 },
    { field: 'componentType', headerName: 'Type', minWidth: 150 },
    { field: 'labelName', headerName: 'Label', minWidth: 180, flex: 1 },
    {
      field: 'sequenceNo',
      headerName: 'Order',
      minWidth: 120,
    },
    {
      field: 'isRequired',
      headerName: 'Required',
      minWidth: 110,
      valueFormatter: (params) => (params.value ? 'Yes' : 'No'),
    },
    {
      field: 'isVisible',
      headerName: 'Visible',
      minWidth: 110,
      valueFormatter: (params) => (params.value ? 'Yes' : 'No'),
    },
  ]

  useEffect(() => {
    const fetchComponents = async () => {
      try {
        const data = await getComponentsByPage(pageCode)
        const list = Array.isArray(data) ? data : []
        setComponents(list)
        setFormData((prev) => ({
          ...prev,
          sequenceNo: list.length + 1,
        }))
      } catch (error) {
        console.error('Failed to load page components', error)
        setComponents([])
        setFormData((prev) => ({
          ...prev,
          sequenceNo: 1,
        }))
      }
    }

    if (pageCode) {
      fetchComponents()
    }
  }, [pageCode])

  const handleChange = (event) => {
    const { name, type, value, checked } = event.target
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : type === 'number' ? Number(value) : value,
    }))
  }

  const handleSelect = (item) => {
    setSelectedComponent(item.type)
    setFormData((prev) => ({
      ...prev,
      componentType: item.type,
      componentName: item.label,
    }))
    setLookupValues([])
    setNewLookupValue('')
  }

  const handleAddLookupValue = () => {
    if (newLookupValue.trim()) {
      setLookupValues((prev) => [
        ...prev,
        {
          id: Date.now(),
          lookupValue: newLookupValue.trim(),
          displayValue: newLookupValue.trim(),
          sequenceNo: prev.length + 1,
          isActive: true,
        },
      ])
      setNewLookupValue('')
    }
  }

  const handleRemoveLookupValue = (id) => {
    setLookupValues((prev) => prev.filter((item) => item.id !== id))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setErrorMessage('')

    if (!formData.componentName.trim() || !formData.labelName.trim() || !formData.width.trim() || !formData.sequenceNo) {
      setErrorMessage('Please fill in every required field except placeholder.')
      return
    }

    if ((formData.componentType === 'select' || formData.componentType === 'radio') && lookupValues.length === 0) {
      setErrorMessage('Please add at least one lookup value for select or radio components.')
      return
    }

    setSaving(true)

    try {
      const properties = {
        label: formData.labelName || undefined,
        placeholder: formData.placeholder || undefined,
        width: formData.width || undefined,
        visible: formData.isVisible,
        disabled: formData.isDisabled,
        required: formData.isRequired,
      }

      if (formData.componentType === 'button') {
        properties.text = formData.labelName || formData.componentName
      }

      const payload = {
        pageCode,
        componentName: formData.componentName,
        componentType: formData.componentType,
        labelName: formData.labelName,
        placeholder: formData.placeholder,
        sequenceNo: formData.sequenceNo,
        isRequired: formData.isRequired,
        isVisible: formData.isVisible,
        isDisabled: formData.isDisabled,
        properties: JSON.stringify(properties),
      }

      // lookupMasterId is managed server-side via relation; do not send it from the UI

      if (lookupValues.length > 0) {
        payload.lookupValues = lookupValues.map(({ id, ...rest }) => ({
          ...rest,
          lookupType: formData.componentType,
        }))
      }

      await createComponent(payload)
      const updated = await getComponentsByPage(pageCode)
      const list = Array.isArray(updated) ? updated : []
      setComponents(list)
      setFormData((prev) => ({
        ...prev,
        componentName: '',
        labelName: '',
        placeholder: '',
        sequenceNo: list.length + 1,
      }))
      setLookupValues([])
      setNewLookupValue('')
      navigate(`./`)
    } catch (error) {
      console.error('Failed to create component', error)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-xl font-semibold text-slate-900">Page Components</h1>
          <p className="text-sm text-slate-500">Manage components available for page <span className="font-semibold">{pageCode}</span>.</p>
        </div>
        <Link
          to="/admin_panel/manage_page"
          className="inline-flex w-fit items-center rounded-full bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-700"
        >
          Back to Pages
        </Link>
      </div>

      <div className="grid gap-6 lg:grid-cols-[280px_1fr]">
        <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 className="mb-3 text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">Available Components</h2>
          <div className="space-y-3">
            {AVAILABLE_COMPONENTS.map((item) => (
              <div
                key={item.type}
                onClick={() => handleSelect(item)}
                role="button"
                tabIndex={0}
                className={`rounded-2xl border p-4 bg-slate-50 hover:shadow-md transition-colors cursor-pointer ${
                  selectedComponent === item.type ? 'border-cyan-400 bg-cyan-50' : 'border-slate-200'
                }`}
              >
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-cyan-500 text-xl text-white shadow-sm">
                    {item.icon}
                  </div>
                  <div>
                    <p className="font-semibold text-slate-900">{item.label}</p>
                    <p className="text-xs uppercase tracking-[0.18em] text-slate-400">type: {item.type}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="space-y-6">
          <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
            <h2 className="mb-4 text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">Add Component</h2>
            <form onSubmit={handleSubmit} className="grid gap-6">
              {errorMessage ? (
                <div className="rounded-xl bg-red-50 border border-red-200 p-4 text-sm text-red-700">
                  {errorMessage}
                </div>
              ) : null}
              {/* Common Fields */}
              <div className="space-y-4 border-b border-slate-200 pb-4">
                <div className="grid gap-2">
                  <label className="text-sm font-medium text-slate-700">Component Name *</label>
                  <input
                    required
                    name="componentName"
                    value={formData.componentName}
                    onChange={handleChange}
                    className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                  />
                </div>

                <div className="grid gap-2">
                  <label className="text-sm font-medium text-slate-700">Component Type</label>
                  <select
                    name="componentType"
                    value={formData.componentType}
                    onChange={handleChange}
                    className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                  >
                    {AVAILABLE_COMPONENTS.map((item) => (
                      <option key={item.type} value={item.type}>
                        {item.label}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="grid gap-2 sm:grid-cols-2">
                  <div className="grid gap-2">
                    <label className="text-sm font-medium text-slate-700">Label *</label>
                    <input
                      required
                      name="labelName"
                      value={formData.labelName}
                      onChange={handleChange}
                      className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                    />
                  </div>
                  <div className="grid gap-2">
                    <label className="text-sm font-medium text-slate-700">Placeholder</label>
                    <input
                      name="placeholder"
                      value={formData.placeholder}
                      onChange={handleChange}
                      className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                    />
                  </div>
                </div>
                <div className="grid gap-2">
                  <label className="text-sm font-medium text-slate-700">Width *</label>
                  <input
                    required
                    name="width"
                    value={formData.width}
                    onChange={handleChange}
                    placeholder="e.g. 200px or 50%"
                    className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                  />
                </div>
                <div className="grid gap-2 sm:grid-cols-2">
                  <div className="grid gap-2">
                    <label className="text-sm font-medium text-slate-700">Sequence *</label>
                    <input
                      required
                      type="number"
                      name="sequenceNo"
                      value={formData.sequenceNo}
                      onChange={handleChange}
                      min={1}
                      className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                    />
                  </div>
                  <div className="grid gap-2">
                    <label className="text-sm font-medium text-slate-700">Status</label>
                    <div className="flex gap-3">
                      <label className="inline-flex items-center gap-2 text-sm text-slate-700">
                        <input
                          type="checkbox"
                          name="isRequired"
                          checked={formData.isRequired}
                          onChange={handleChange}
                          className="h-4 w-4 rounded border-slate-300 text-cyan-500"
                        />
                        Required
                      </label>
                      <label className="inline-flex items-center gap-2 text-sm text-slate-700">
                        <input
                          type="checkbox"
                          name="isVisible"
                          checked={formData.isVisible}
                          onChange={handleChange}
                          className="h-4 w-4 rounded border-slate-300 text-cyan-500"
                        />
                        Visible
                      </label>
                    </div>
                  </div>
                </div>
              </div>

              {/* Type-Specific Configuration */}
              {(formData.componentType === 'select' || formData.componentType === 'radio') && (
                <div className="space-y-4">
                  <h3 className="text-sm font-semibold text-slate-900">Values for {formData.componentType}</h3>
                
                <div className="flex gap-2">
                    <input
                      type="text"
                      value={newLookupValue}
                      onChange={(e) => setNewLookupValue(e.target.value)}
                      placeholder="Enter value (e.g., Option 1)"
                      className="flex-1 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                      onKeyPress={(e) => {
                        if (e.key === 'Enter') {
                          e.preventDefault()
                          handleAddLookupValue()
                        }
                      }}
                    />
                    <button
                      type="button"
                      onClick={handleAddLookupValue}
                      className="inline-flex items-center rounded-xl bg-slate-100 px-4 py-2 text-sm font-semibold text-slate-900 transition hover:bg-slate-200"
                    >
                      Add
                    </button>
                  </div>

                  {lookupValues.length > 0 && (
                    <div className="space-y-2">
                      {lookupValues.map((item, idx) => (
                        <div key={item.id} className="flex items-center justify-between rounded-xl border border-slate-200 bg-slate-50 p-3">
                          <div className="flex items-center gap-3">
                            <span className="text-xs font-semibold text-slate-500">{idx + 1}</span>
                            <span className="text-sm text-slate-900">{item.lookupValue}</span>
                          </div>
                          <button
                            type="button"
                            onClick={() => handleRemoveLookupValue(item.id)}
                            className="text-xs font-semibold text-red-500 transition hover:text-red-700"
                          >
                            Remove
                          </button>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}

              <button
                type="submit"
                disabled={saving || (formData.componentType === 'select' || formData.componentType === 'radio') && lookupValues.length === 0}
                className="inline-flex items-center justify-center rounded-full bg-cyan-500 px-5 py-2 text-sm font-semibold text-white transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {saving ? 'Adding...' : 'Add Component'}
              </button>
            </form>
          </div>

          <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
            <div className="mb-4 flex items-center justify-between">
              <div>
                <h2 className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">Components on this page</h2>
                <p className="text-sm text-slate-500">Review the page-specific component list below.</p>
              </div>
            </div>
            <div className="overflow-hidden rounded-2xl border border-slate-200">
              <div className="h-[360px] w-full">
                <AgGridReact rowData={components} columnDefs={columnDefs} />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
