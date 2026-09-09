import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { createComponent, deleteComponent, getComponentsByPage, updateComponent } from '../../../../api/componentApi'
import { getActionsByPageCode } from '../../../../api/actionsPageApi'
import { getUiPageByCode } from '../../../../api/uiPageApi'
import ComponentPalette, { AVAILABLE_COMPONENTS } from './components/ComponentPalette'
import PagePreviewPanel from './components/PagePreviewPanel'
import PageComponentList from './components/PageComponentList'
import ComponentEditorPanel from './components/ComponentEditorPanel'
import ComponentActionModal from './components/ComponentActionModel'

const CHILD_PARENT_ALLOWED_TYPES = new Set(['card', 'layout'])

const EMPTY_ADDITIONAL_PROPERTIES = { height: '', border: '', borderRadius: '', className: '' }
const EMPTY_MAPPING_VALUES = {
  tableName: '',
  columnName: '',
  attributeName: '',
  displayName: '',
  isRequired: false,
  isFilterable: false,
}

// Server row -> internal draft shape. Used both to seed draftComponents on
// load and to populate the editor when a row is selected for editing.
function apiRowToDraft(row) {
  let parsedProperties = {}
  try { parsedProperties = row?.properties ? JSON.parse(row.properties) : {} } catch { parsedProperties = {} }

  return {
    id: row.id,
    componentName: row.componentName || '',
    componentType: row.componentType || AVAILABLE_COMPONENTS[0].type,
    labelName: row.labelName || parsedProperties.label || '',
    placeholder: row.placeholder || parsedProperties.placeholder || '',
    width: parsedProperties.width || '200px',
    sequenceNo: row.sequenceNo || 1,
    parentComponentId: row.parentComponentId
      ? Number(row.parentComponentId)
      : parsedProperties.parentComponentId
        ? Number(parsedProperties.parentComponentId)
        : null,
    isRequired: !!row.isRequired,
    isVisible: row.isVisible ?? true,
    isDisabled: row.isDisabled ?? false,
    additionalProperties: {
      height: parsedProperties.height || '',
      border: parsedProperties.border || '',
      borderRadius: parsedProperties.borderRadius || '',
      className: parsedProperties.className || '',
    },
    mappingValues: {
      tableName: row.tableName || '',
      columnName: row.columnName || '',
      attributeName: row.attributeName || '',
      displayName: row.displayName || '',
      isRequired: row.mappingRequired ?? false,
      isFilterable: row.isFilterable ?? false,
    },
    lookupValues: Array.isArray(row.lookupValues) ? row.lookupValues : [],
  }
}

function draftToEditorDraft(draft) {
  return {
    componentName: draft.componentName,
    componentType: draft.componentType,
    labelName: draft.labelName,
    placeholder: draft.placeholder,
    width: draft.width,
    sequenceNo: draft.sequenceNo,
    isChildComponent: !!draft.parentComponentId,
    parentComponentId: draft.parentComponentId ? String(draft.parentComponentId) : '',
    isRequired: draft.isRequired,
    isVisible: draft.isVisible,
    isDisabled: draft.isDisabled,
    additionalProperties: { ...draft.additionalProperties },
    mappingValues: { ...draft.mappingValues },
    lookupValues: [...draft.lookupValues],
  }
}

function defaultEditorDraft(paletteItem, sequenceNo) {
  return {
    componentName: paletteItem.label,
    componentType: paletteItem.type,
    labelName: '',
    placeholder: '',
    width: '',
    sequenceNo,
    isChildComponent: false,
    parentComponentId: '',
    isRequired: false,
    isVisible: true,
    isDisabled: false,
    additionalProperties: { ...EMPTY_ADDITIONAL_PROPERTIES },
    mappingValues: { ...EMPTY_MAPPING_VALUES },
    lookupValues: [],
  }
}

export default function ManagePageComponents() {
  const { pageCode } = useParams()

  const [draftComponents, setDraftComponents] = useState([])
  const [pageJson, setPageJson] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const [editorMode, setEditorMode] = useState('add') // 'add' | 'edit'
  const [selectedComponentId, setSelectedComponentId] = useState(null)
  const [editorDraft, setEditorDraft] = useState(defaultEditorDraft(AVAILABLE_COMPONENTS[0], 1))
  const [newLookupValue, setNewLookupValue] = useState('')
  const [parentSearch, setParentSearch] = useState('')

  const [pageActions, setPageActions] = useState([])
  const [actionModalComponent, setActionModalComponent] = useState(null)

  // Loads both the component rows (right-list + editor source) and the
  // fully assembled page JSON (live preview source — same endpoint real
  // pages render from) in parallel, so they always reflect the same
  // server state after every save.
  const loadComponents = async () => {
    setIsLoading(true)
    try {
      const [componentsData, pageJsonData] = await Promise.all([
        getComponentsByPage(pageCode),
        getUiPageByCode(pageCode),
      ])
      const list = Array.isArray(componentsData) ? componentsData : []
      setDraftComponents(list.map(apiRowToDraft))
      setPageJson(pageJsonData?.jsonSchema || null)
    } catch (error) {
      console.error('Failed to load page components', error)
      setDraftComponents([])
      setPageJson(null)
    } finally {
      setIsLoading(false)
    }
  }

  const loadPageActions = async () => {
    try {
      const data = await getActionsByPageCode(pageCode)
      setPageActions(Array.isArray(data) ? data : [])
    } catch (err) {
      console.error('Failed to load page actions', err)
      setPageActions([])
    }
  }

  useEffect(() => {
    if (pageCode) {
      loadComponents()
      loadPageActions()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pageCode])

  const resetToAddMode = () => {
    setEditorMode('add')
    setSelectedComponentId(null)
    setEditorDraft(defaultEditorDraft(AVAILABLE_COMPONENTS[0], draftComponents.length + 1))
    setNewLookupValue('')
    setParentSearch('')
    setErrorMessage('')
  }

  const handlePaletteSelect = (item) => {
    setEditorMode('add')
    setSelectedComponentId(null)
    setEditorDraft(defaultEditorDraft(item, draftComponents.length + 1))
    setNewLookupValue('')
    setParentSearch('')
    setErrorMessage('')
  }

  const handleListSelect = (component) => {
    setEditorMode('edit')
    setSelectedComponentId(component.id)
    setEditorDraft(draftToEditorDraft(component))
    setNewLookupValue('')
    setParentSearch('')
    setErrorMessage('')
  }

  const handleFieldChange = (event) => {
    const { name, type, value, checked } = event.target
    const nextValue = type === 'checkbox' ? checked : type === 'number' ? Number(value) : value

    setEditorDraft((prev) => {
      if (name === 'isChildComponent' && !nextValue) {
        return { ...prev, isChildComponent: false, parentComponentId: '' }
      }
      const result = { ...prev, [name]: nextValue }
      return result
    })

    if (name === 'isChildComponent' && !checked) setParentSearch('')
  }

  const handleAdditionalPropertyChange = (field, value) => {
    setEditorDraft((prev) => ({ ...prev, additionalProperties: { ...prev.additionalProperties, [field]: value } }))
  }

  const handleMappingValueChange = (field, value) => {
    setEditorDraft((prev) => ({ ...prev, mappingValues: { ...prev.mappingValues, [field]: value } }))
  }

  const handleAddLookupValue = () => {
    if (!newLookupValue.trim()) return
    setEditorDraft((prev) => ({
      ...prev,
      lookupValues: [
        ...prev.lookupValues,
        {
          id: Date.now(),
          lookupValue: newLookupValue.trim(),
          displayValue: newLookupValue.trim(),
          sequenceNo: prev.lookupValues.length + 1,
          isActive: true,
        },
      ],
    }))
    setNewLookupValue('')
  }

  const handleRemoveLookupValue = (id) => {
    setEditorDraft((prev) => ({ ...prev, lookupValues: prev.lookupValues.filter((item) => item.id !== id) }))
  }

  const handleDelete = async (id) => {
    const confirmed = window.confirm('Delete this component?')
    if (!confirmed) return
    try {
      await deleteComponent(id)
      await loadComponents()
      if (selectedComponentId === id) resetToAddMode()
    } catch (error) {
      console.error('Failed to delete component', error)
    }
  }

  const parentOptions = draftComponents
    .filter((c) => c.id !== selectedComponentId)
    .filter((c) => CHILD_PARENT_ALLOWED_TYPES.has(c.componentType))
    .filter((c) => {
      if (!parentSearch.trim()) return true
      const s = parentSearch.trim().toLowerCase()
      return String(c.id).includes(s) || (c.componentName || '').toLowerCase().includes(s)
    })

  const handleSubmit = async (event) => {
    event.preventDefault()
    setErrorMessage('')

    if (!editorDraft.componentName.trim() || !editorDraft.labelName.trim() || !editorDraft.width.trim() || !editorDraft.sequenceNo) {
      setErrorMessage('Please fill in every required field except placeholder.')
      return
    }
    if (editorDraft.isChildComponent && !editorDraft.parentComponentId) {
      setErrorMessage('Please choose a parent component (card/layout) for this child component.')
      return
    }
    if ((editorDraft.componentType === 'select' || editorDraft.componentType === 'radio') && editorDraft.lookupValues.length === 0) {
      setErrorMessage('Please add at least one lookup value for select or radio components.')
      return
    }

    setSaving(true)
    try {
      const isTableComponent = editorDraft.componentType === 'table'
      const properties = Object.fromEntries(
        Object.entries(editorDraft.additionalProperties).filter(([, v]) => v !== '' && v !== null && v !== undefined)
      )
      if (isTableComponent) properties.title = editorDraft.labelName || editorDraft.componentName
      if (editorDraft.componentType === 'button') properties.text = editorDraft.labelName || editorDraft.componentName

      const payload = {
        component: {
          pageCode,
          componentName: editorDraft.componentName,
          componentType: editorDraft.componentType,
          labelName: editorDraft.labelName,
          placeholder: editorDraft.placeholder,
          sequenceNo: editorDraft.sequenceNo,
          parentComponentId: editorDraft.isChildComponent ? Number(editorDraft.parentComponentId) : null,
          isRequired: editorDraft.isRequired,
          isVisible: editorDraft.isVisible,
          isDisabled: editorDraft.isDisabled,
          properties: JSON.stringify(properties),
        },
        mappingValues: editorDraft.mappingValues,
      }

      if (editorDraft.lookupValues.length > 0) {
        payload.lookupValues = editorDraft.lookupValues.map(({ id, ...rest }) => ({
          ...rest,
          lookupType: editorDraft.componentType,
        }))
      }

      if (editorMode === 'edit' && selectedComponentId) {
        await updateComponent(selectedComponentId, payload)
      } else {
        await createComponent(payload)
      }

      await loadComponents()
      resetToAddMode()
    } catch (error) {
      console.error('Failed to save component', error)
    } finally {
      setSaving(false)
    }
  }

  return (
    <>
      <div className="flex h-full flex-col gap-4">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-xl font-semibold text-slate-900">Page Components</h1>
            <p className="text-sm text-slate-500">
              Page <span className="font-semibold">{pageCode}</span>
            </p>
          </div>
          <Link
            to="/admin_panel/manage_page"
            className="inline-flex w-fit items-center rounded-full bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-700"
          >
            Back to Pages
          </Link>
        </div>

        <div className="grid min-h-140 flex-1 gap-4 lg:grid-cols-[20%_58%_20%]">
          <ComponentPalette activeType={editorMode === 'add' ? editorDraft.componentType : null} onSelect={handlePaletteSelect} />
          <PagePreviewPanel
            pageJson={pageJson}
            editorMode={editorMode}
            selectedComponentId={selectedComponentId}
            editorDraft={editorDraft}
          />
          <PageComponentList
            components={draftComponents}
            selectedComponentId={selectedComponentId}
            onSelect={handleListSelect}
            onOpenActions={setActionModalComponent}
            onDelete={handleDelete}
            isLoading={isLoading}
          />
        </div>

        <ComponentEditorPanel
          mode={editorMode}
          editorDraft={editorDraft}
          onFieldChange={handleFieldChange}
          onAdditionalPropertyChange={handleAdditionalPropertyChange}
          onMappingValueChange={handleMappingValueChange}
          parentSearch={parentSearch}
          onParentSearchChange={setParentSearch}
          parentOptions={parentOptions}
          newLookupValue={newLookupValue}
          onNewLookupValueChange={setNewLookupValue}
          onAddLookupValue={handleAddLookupValue}
          onRemoveLookupValue={handleRemoveLookupValue}
          errorMessage={errorMessage}
          saving={saving}
          onSubmit={handleSubmit}
          onCancel={resetToAddMode}
        />
      </div>

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
