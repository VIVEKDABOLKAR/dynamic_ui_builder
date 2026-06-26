import React, { useEffect, useState } from 'react'
import { AgGridReact } from 'ag-grid-react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { createComponent, deleteComponent, getComponentsByPage, updateComponent } from '../../../../api/componentApi'
import AdditionalPropertiesPanel from './components/AdditionalPropertiesPanel'
import DatabaseMappingPanel from './components/DatabaseMappingPanel'
import ComponentActionModal from './components/ComponentActionModel'
import { getActionsByPageCode } from '../../../../api/actionsPageApi'

const AVAILABLE_COMPONENTS = [
  { type: 'input', label: 'Text Field', icon: '🔤' },
  { type: 'textarea', label: 'Text Area', icon: '📝' },
  { type: 'button', label: 'Button', icon: '🔘' },
  { type: 'select', label: 'Dropdown', icon: '🔽' },
  { type: 'radio', label: 'Radio Button', icon: '⭕' },
  { type: 'checkbox', label: 'Checkbox', icon: '☑️' },
  { type: 'table', label: 'Data Table', icon: '📊' },
  { type: 'card', label: 'Card', icon: '🗂️' },
  { type: 'datepicker', label: 'DatePicker', icon: '📅' },
  { type: 'layout', label: 'Layout', icon: '📐' },
]

const CHILD_PARENT_ALLOWED_TYPES = new Set(['card', 'layout'])

export default function ManagePageComponents() {
  const { pageCode } = useParams()
  const navigate = useNavigate()

  const [actionModalComponent, setActionModalComponent] = useState(null)
  // Load page-level actions so the modal can show a ref dropdown
  const [pageActions, setPageActions] = useState([])

  const [components, setComponents] = useState([])
  const [isLoading, setIsLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [editingComponentId, setEditingComponentId] = useState(null)
  const [selectedComponent, setSelectedComponent] = useState(null)
  const [errorMessage, setErrorMessage] = useState('')
  const [formData, setFormData] = useState({
    componentName: '',
    componentType: AVAILABLE_COMPONENTS[0].type,
    labelName: '',
    placeholder: '',
    width: '',
    sequenceNo: 1,
    isChildComponent: false,
    parentComponentId: '',
    isRequired: false,
    isVisible: true,
    isDisabled: false,
  })
  const [lookupValues, setLookupValues] = useState([])
  const [newLookupValue, setNewLookupValue] = useState('')
  const [parentSearch, setParentSearch] = useState('')
  const [additionalProperties, setAdditionalProperties] = useState({
    height: '',
    border: '',
    borderRadius: '',
    className: '',
  })
  const [mappingValues, setMappingValues] = useState({
    tableName: '',
    columnName: '',
    attributeName: '',
    displayName: '',
    isRequired: false,
    isFilterable: false,
  })

  const isTableComponent = formData.componentType === 'table'

  const loadComponents = async () => {
    setIsLoading(true)
    try {
      const data = await getComponentsByPage(pageCode)
      const list = Array.isArray(data) ? data : []
      setComponents(list)
      setFormData((prev) => ({ ...prev, sequenceNo: list.length + 1 }))
    } catch (error) {
      console.error('Failed to load page components', error)
      setComponents([])
      setFormData((prev) => ({ ...prev, sequenceNo: 1 }))
    } finally {
      setIsLoading(false)
    }
  }

  // Load page-level actions once so the ComponentActionModal can show them
  // in the action ref dropdown
  const loadPageActions = async () => {
    try {
      const data = await getActionsByPageCode(pageCode)
      setPageActions(Array.isArray(data) ? data : [])
    } catch (err) {
      console.error('Failed to load page actions', err)
      setPageActions([])
    }
  }

  const resetForm = () => {
    setEditingComponentId(null)
    setSelectedComponent(null)
    setFormData((prev) => ({
      ...prev,
      componentName: '',
      componentType: AVAILABLE_COMPONENTS[0].type,
      labelName: '',
      placeholder: '',
      width: '',
      isChildComponent: false,
      parentComponentId: '',
      isRequired: false,
      isVisible: true,
      isDisabled: false,
    }))
    setLookupValues([])
    setNewLookupValue('')
    setParentSearch('')
    setAdditionalProperties({ height: '', border: '', borderRadius: '', className: '' })
    setMappingValues({
      tableName: '',
      columnName: '',
      attributeName: '',
      displayName: '',
      isRequired: false,
      isFilterable: false,
    })
  }

  const handleEditRow = (row) => {
    let parsedProperties = {}
    try { parsedProperties = row?.properties ? JSON.parse(row.properties) : {} } catch { parsedProperties = {} }

    setEditingComponentId(row.id)
    setSelectedComponent(row.componentType)
    setFormData((prev) => ({
      ...prev,
      componentName: row.componentName || '',
      componentType: row.componentType || AVAILABLE_COMPONENTS[0].type,
      labelName: row.labelName || parsedProperties.label || '',
      placeholder: row.placeholder || parsedProperties.placeholder || '',
      width: parsedProperties.width || '200px',
      sequenceNo: row.sequenceNo || 1,
      isChildComponent: !!(row.parentComponentId || parsedProperties.parentComponentId),
      parentComponentId: row.parentComponentId
        ? String(row.parentComponentId)
        : parsedProperties.parentComponentId
          ? String(parsedProperties.parentComponentId)
          : '',
      isRequired: !!row.isRequired,
      isVisible: row.isVisible ?? true,
      isDisabled: row.isDisabled ?? false,
    }))
    setLookupValues(Array.isArray(row.lookupValues) ? row.lookupValues : [])
    setParentSearch('')
    setAdditionalProperties({
      height: parsedProperties.height || '',
      border: parsedProperties.border || '',
      borderRadius: parsedProperties.borderRadius || '',
      className: parsedProperties.className || '',
    })
    setMappingValues({
      tableName: row.tableName || '',
      columnName: row.columnName || '',
      attributeName: row.attributeName || '',
      displayName: row.displayName || '',
      isRequired: row.mappingRequired ?? false,
      isFilterable: row.isFilterable ?? false,
    })
  }

  const handleDeleteRow = async (id) => {
    const confirmed = window.confirm('Delete this component?')
    if (!confirmed) return
    try {
      await deleteComponent(id)
      await loadComponents()
      if (editingComponentId === id) resetForm()
    } catch (error) {
      console.error('Failed to delete component', error)
    }
  }

  const columnDefs = [
    { field: 'id', hide: true },
    { field: 'componentName', headerName: 'Name', minWidth: 180, flex: 1 },
    { field: 'componentType', headerName: 'Type', minWidth: 150 },
    { field: 'labelName', headerName: 'Label', minWidth: 180, flex: 1 },
    { field: 'sequenceNo', headerName: 'Order', minWidth: 100 },
    {
      field: 'isRequired',
      headerName: 'Required',
      minWidth: 100,
      valueFormatter: (p) => (p.value ? 'Yes' : 'No'),
    },
    {
      field: 'isVisible',
      headerName: 'Visible',
      minWidth: 100,
      valueFormatter: (p) => (p.value ? 'Yes' : 'No'),
    },
    {
      headerName: 'Actions',
      field: 'actions',
      pinned: 'right',
      lockPinned: true,
      suppressMovable: true,
      sortable: false,
      filter: false,
      minWidth: 210,
      maxWidth: 240,
      cellRenderer: (params) => (
        <div className="flex h-full items-center gap-2 py-1">
          <button
            type="button"
            onClick={() => handleEditRow(params.data)}
            className="rounded-md bg-amber-100 px-2 py-1 text-xs font-semibold text-amber-700 hover:bg-amber-200"
          >
            Edit
          </button>
          <button
            type="button"
            onClick={() => setActionModalComponent(params.data)}
            className="rounded-md bg-blue-100 px-2 py-1 text-xs font-semibold text-blue-800 hover:bg-blue-200"
          >
            Actions
          </button>
          <button
            type="button"
            onClick={() => handleDeleteRow(params.data.id)}
            className="rounded-md bg-red-100 px-2 py-1 text-xs font-semibold text-red-700 hover:bg-red-200"
          >
            Delete
          </button>
        </div>
      ),
    },
  ]

  useEffect(() => {
    if (pageCode) {
      loadComponents()
      loadPageActions()
    }
  }, [pageCode])

  const handleChange = (event) => {
    const { name, type, value, checked } = event.target
    const nextValue = type === 'checkbox' ? checked : type === 'number' ? Number(value) : value

    setFormData((prev) => {
      if (name === 'isChildComponent' && !nextValue) {
        return { ...prev, isChildComponent: false, parentComponentId: '' }
      }
      return { ...prev, [name]: nextValue }
    })

    if (name === 'isChildComponent' && !checked) setParentSearch('')
  }

  const handleSelect = (item) => {
    setSelectedComponent(item.type)
    setFormData((prev) => ({ ...prev, componentType: item.type, componentName: item.label }))
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

  const handleAdditionalPropertyChange = (field, value) => {
    setAdditionalProperties((prev) => ({ ...prev, [field]: value }))
  }

  const handleMappingValueChange = (field, value) => {
    setMappingValues((prev) => ({ ...prev, [field]: value }))
  }

  const parentOptions = components
    .filter((c) => c.id !== editingComponentId)
    .filter((c) => CHILD_PARENT_ALLOWED_TYPES.has(c.componentType))
    .filter((c) => {
      if (!parentSearch.trim()) return true
      const s = parentSearch.trim().toLowerCase()
      return String(c.id).includes(s) || (c.componentName || '').toLowerCase().includes(s)
    })

  const handleSubmit = async (event) => {
    event.preventDefault()
    setErrorMessage('')

    if (!formData.componentName.trim() || !formData.labelName.trim() || !formData.width.trim() || !formData.sequenceNo) {
      setErrorMessage('Please fill in every required field except placeholder.')
      return
    }
    if (formData.isChildComponent && !formData.parentComponentId) {
      setErrorMessage('Please choose a parent component (card/layout) for this child component.')
      return
    }
    if ((formData.componentType === 'select' || formData.componentType === 'radio') && lookupValues.length === 0) {
      setErrorMessage('Please add at least one lookup value for select or radio components.')
      return
    }

    setSaving(true)
    try {
      const properties = Object.fromEntries(
        Object.entries(additionalProperties).filter(([, v]) => v !== '' && v !== null && v !== undefined)
      )
      if (isTableComponent) properties.title = formData.labelName || formData.componentName
      if (formData.componentType === 'button') properties.text = formData.labelName || formData.componentName

      const payload = {
        component: {
          pageCode,
          componentName: formData.componentName,
          componentType: formData.componentType,
          labelName: formData.labelName,
          placeholder: formData.placeholder,
          sequenceNo: formData.sequenceNo,
          parentComponentId: formData.isChildComponent ? Number(formData.parentComponentId) : null,
          isRequired: formData.isRequired,
          isVisible: formData.isVisible,
          isDisabled: formData.isDisabled,
          properties: JSON.stringify(properties),
        },
        mappingValues,
      }

      if (lookupValues.length > 0) {
        payload.lookupValues = lookupValues.map(({ id, ...rest }) => ({
          ...rest,
          lookupType: formData.componentType,
        }))
      }

      if (editingComponentId) {
        await updateComponent(editingComponentId, payload)
      } else {
        await createComponent(payload)
      }

      await loadComponents()
      resetForm()
      navigate('./')
    } catch (error) {
      console.error('Failed to save component', error)
    } finally {
      setSaving(false)
    }
  }

  return (
    <>
      <div className="space-y-6">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-xl font-semibold text-slate-900">Page Components</h1>
            <p className="text-sm text-slate-500">
              Manage components for page <span className="font-semibold">{pageCode}</span>.
            </p>
          </div>
          <Link
            to="/admin_panel/manage_page"
            className="inline-flex w-fit items-center rounded-full bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-700"
          >
            Back to Pages
          </Link>
        </div>

        <div className="grid gap-6 lg:grid-cols-[280px_1fr]">
          {/* component palette */}
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
            {/* add/edit form */}
            <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
              <h2 className="mb-4 text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
                {editingComponentId ? 'Edit Component' : 'Add Component'}
              </h2>
              <form onSubmit={handleSubmit} className="grid gap-6">
                {errorMessage && (
                  <div className="rounded-xl bg-red-50 border border-red-200 p-4 text-sm text-red-700">
                    {errorMessage}
                  </div>
                )}

                <div className="space-y-4 border-b border-slate-200 pb-4">
                  <div className="grid gap-2">
                    <label className="text-sm font-medium text-slate-700">Component Name *</label>
                    <input
                      required
                      name="componentName"
                      value={formData.componentName}
                      onChange={handleChange}
                      className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                    />
                  </div>

                  <div className="grid gap-2">
                    <label className="text-sm font-medium text-slate-700">Component Type</label>
                    <select
                      name="componentType"
                      value={formData.componentType}
                      onChange={handleChange}
                      className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                    >
                      {AVAILABLE_COMPONENTS.map((item) => (
                        <option key={item.type} value={item.type}>{item.label}</option>
                      ))}
                    </select>
                  </div>

                  <div className="grid gap-2 sm:grid-cols-2">
                    <div className="grid gap-2">
                      <label className="text-sm font-medium text-slate-700">
                        {isTableComponent ? 'Table Title *' : 'Label *'}
                      </label>
                      <input
                        required
                        name="labelName"
                        value={formData.labelName}
                        onChange={handleChange}
                        className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                      />
                    </div>
                    <div className="grid gap-2">
                      <label className="text-sm font-medium text-slate-700">Placeholder</label>
                      <input
                        name="placeholder"
                        value={formData.placeholder}
                        onChange={handleChange}
                        className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                      />
                    </div>
                  </div>

                  <div className="grid gap-2 sm:grid-cols-2">
                    <div className="grid gap-2">
                      <label className="text-sm font-medium text-slate-700">Width *</label>
                      <input
                        required
                        name="width"
                        value={formData.width}
                        onChange={handleChange}
                        placeholder="e.g. 200px or 50%"
                        className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                      />
                    </div>
                    <div className="grid gap-2">
                      <label className="text-sm font-medium text-slate-700">Sequence *</label>
                      <input
                        required
                        type="number"
                        name="sequenceNo"
                        value={formData.sequenceNo}
                        onChange={handleChange}
                        min={1}
                        className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                      />
                    </div>
                  </div>

                  <div className="flex gap-4">
                    {['isRequired', 'isVisible', 'isDisabled'].map((field) => (
                      <label key={field} className="inline-flex items-center gap-2 text-sm text-slate-700">
                        <input
                          type="checkbox"
                          name={field}
                          checked={formData[field]}
                          onChange={handleChange}
                          className="h-4 w-4 rounded border-slate-300 text-cyan-500"
                        />
                        {field.replace('is', '')}
                      </label>
                    ))}
                  </div>

                  <div className="space-y-3 rounded-xl border border-slate-200 bg-slate-50 p-4">
                    <label className="inline-flex items-center gap-2 text-sm font-medium text-slate-700">
                      <input
                        type="checkbox"
                        name="isChildComponent"
                        checked={formData.isChildComponent}
                        onChange={handleChange}
                        className="h-4 w-4 rounded border-slate-300 text-cyan-500"
                      />
                      Add as child component
                    </label>
                    {formData.isChildComponent && (
                      <div className="grid gap-3">
                        <input
                          value={parentSearch}
                          onChange={(e) => setParentSearch(e.target.value)}
                          placeholder="Search parent by id or name"
                          className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                        />
                        <select
                          name="parentComponentId"
                          value={formData.parentComponentId}
                          onChange={handleChange}
                          className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                        >
                          <option value="">Select parent component</option>
                          {parentOptions.map((item) => (
                            <option key={item.id} value={item.id}>
                              {item.id} - {item.componentName} ({item.componentType})
                            </option>
                          ))}
                        </select>
                        {parentOptions.length === 0 && (
                          <p className="text-xs text-amber-600">No card/layout components found for this page.</p>
                        )}
                      </div>
                    )}
                  </div>
                </div>

                <AdditionalPropertiesPanel value={additionalProperties} onChange={handleAdditionalPropertyChange} />
                <DatabaseMappingPanel value={mappingValues} onChange={handleMappingValueChange} />

                {(formData.componentType === 'select' || formData.componentType === 'radio') && (
                  <div className="space-y-4 rounded-2xl border border-slate-200 bg-slate-50 p-4">
                    <h3 className="text-sm font-semibold text-slate-900">Lookup Values</h3>
                    <div className="flex gap-2">
                      <input
                        type="text"
                        value={newLookupValue}
                        onChange={(e) => setNewLookupValue(e.target.value)}
                        placeholder="Enter value"
                        className="flex-1 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                        onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); handleAddLookupValue() } }}
                      />
                      <button
                        type="button"
                        onClick={handleAddLookupValue}
                        className="inline-flex items-center rounded-xl bg-slate-100 px-4 py-2 text-sm font-semibold text-slate-900 hover:bg-slate-200"
                      >
                        Add
                      </button>
                    </div>
                    {lookupValues.length > 0 && (
                      <div className="space-y-2">
                        {lookupValues.map((item, idx) => (
                          <div key={item.id} className="flex items-center justify-between rounded-xl border border-slate-200 bg-white p-3">
                            <div className="flex items-center gap-3">
                              <span className="text-xs font-semibold text-slate-500">{idx + 1}</span>
                              <span className="text-sm text-slate-900">{item.lookupValue}</span>
                            </div>
                            <button
                              type="button"
                              onClick={() => handleRemoveLookupValue(item.id)}
                              className="text-xs font-semibold text-red-500 hover:text-red-700"
                            >
                              Remove
                            </button>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                )}

                <div className="flex gap-2">
                  <button
                    type="submit"
                    disabled={saving}
                    className="inline-flex items-center justify-center rounded-full bg-cyan-500 px-5 py-2 text-sm font-semibold text-white hover:bg-cyan-400 disabled:opacity-60"
                  >
                    {saving
                      ? editingComponentId ? 'Updating…' : 'Adding…'
                      : editingComponentId ? 'Update Component' : 'Add Component'}
                  </button>
                  {editingComponentId && (
                    <button
                      type="button"
                      onClick={resetForm}
                      className="inline-flex items-center justify-center rounded-full bg-slate-200 px-5 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-300"
                    >
                      Cancel Edit
                    </button>
                  )}
                </div>
              </form>
            </div>

            {/* component list */}
            <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
              <div className="mb-4">
                <h2 className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">Components on this page</h2>
                <p className="text-sm text-slate-500">Click <strong>Actions</strong> on any row to manage its event bindings.</p>
              </div>
              <div className="overflow-hidden rounded-2xl border border-slate-200">
                <div className="h-[360px] w-full">
                  {isLoading ? (
                    <div className="flex h-full items-center justify-center gap-3 text-sm text-slate-500">
                      <span className="h-5 w-5 animate-spin rounded-full border-2 border-slate-300 border-t-cyan-500" />
                      Loading components…
                    </div>
                  ) : components.length === 0 ? (
                    <div className="flex h-full flex-col items-center justify-center gap-2 text-sm text-slate-500">
                      <p className="font-semibold text-slate-700">No components yet</p>
                      <p>Add your first component using the form above.</p>
                    </div>
                  ) : (
                    <AgGridReact rowData={components} columnDefs={columnDefs} />
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Component action CRUD modal */}
      {actionModalComponent && (
        <ComponentActionModal
          component={actionModalComponent}
          pageActions={pageActions}
          onClose={() => setActionModalComponent(null)}
        />
      )}
    </>
  )
}